package com.example.SAPLM.bluetoothActivities;

import com.example.SAPLM.R;
import com.example.SAPLM.commandActivity.CommandsActivity;
import com.example.SAPLM.settingsActivities.DeveloperConsoleActivity;
import com.example.SAPLM.settingsActivities.TerminalManager;


import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class UnifiedTransmissionProtocol {

	public static List<Module> modulesList = new ArrayList<UnifiedTransmissionProtocol.Module>(Arrays.asList(
			new Module("Dispositivo Movil", "PHONE", (byte)0x00, R.drawable.ic_icon_phone_module, new ArrayList<Module.Device>(Arrays.asList(
					new Module.Device("Módulo principal", new Boolean(false), R.drawable.ic_icon_main_board, (byte)0x00, new Boolean(true))
			))),
			new Module("Módulo principal","MAIN", (byte)0x01, R.drawable.ic_icon_main_board, new ArrayList<Module.Device>(Arrays.asList(
					new Module.Device("Módulo de potencia", new Boolean(false), R.drawable.icons_board, (byte)0x00, new Boolean(true)),
					new Module.Device("Módulo motriz", new Boolean(false), R.drawable.icons_board, (byte)0x01, new Boolean(true))
			))),
			new Module("Módulo de potencia","POWER", (byte)0x02, R.drawable.icons_board, new ArrayList<Module.Device>(Arrays.asList(
					new Module.Device("Velador", new Boolean(false), R.drawable.icons_desk_lamp, (byte)0x00, new Boolean(false)),
					new Module.Device("Luz", new Boolean(false), R.drawable.icons_bulb, (byte)0x01, new Boolean(false)),
					new Module.Device("Ventilador", new Boolean(false), R.drawable.icons_fan, (byte)0x02, new Boolean(false)),
					new Module.Device("Estufa", new Boolean(false), R.drawable.icons_heater, (byte)0x03, new Boolean(false))
			))),
			new Module("Módulo motriz","MOTOR", (byte)0x03, R.drawable.icons_board, new ArrayList<Module.Device>(Arrays.asList(
					new Module.Device("Camilla", new Byte((byte)0), R.drawable.icons_stretcher, (byte)0x00, new Boolean(false)),
					new Module.Device("Cortina", new Byte((byte)0), R.drawable.icons_curtain, (byte)0x01, new Boolean(false)),
					new Module.Device("Llamar enfermera", new Boolean(false), R.drawable.icons_nurse, (byte)0x02, new Boolean(false))
			)))
	));

	protected static byte[] command_buffer = new byte[32];
	
	private static Module currentModuleID = Module.getLocalModule();
	public static Module lastMessageTransmitterModule = new Module();
	public static byte lastMessagePID;
	private static CommandType lastCommandType;
	private static List<Parameter> lastParameterList;
	private static boolean isAvailableCommand;


	public static byte[] getCommand_buffer() {
		return command_buffer;
	}


	
	
	
	
	public static class Parameter {
		private byte[] parameterData;
		
		Parameter(byte[] parameterData){
			this.parameterData = parameterData;
		}
		
		public byte[] getParameterData() {
			return parameterData;
		}
		
		public byte getParameterLength() {
			return (byte)parameterData.length;
		}
	}
	
	
	
	public static CommandStatus ComposeMessageToBuffer(CommandType targetType, Module targetModule, List<Parameter> parameterList) {
		command_buffer = new byte[32];
		command_buffer[0] = SectionDividersChar.SOH.getCode();
		if (lastMessagePID!=0xFF) { lastMessagePID++; } else { lastMessagePID=(byte)0x00; }
		command_buffer[1] =  lastMessagePID;
		command_buffer[2] =  targetModule.getInternalCode(); // Final receiver
		command_buffer[3] =  currentModuleID.getInternalCode();	// Original transmitter
		command_buffer[4] = SectionDividersChar.STX.getCode();
		command_buffer[5] = targetType.getCommandCode();
		
		if (parameterList.size()>12) return CommandStatus.PARAMETER_COUNT_OVERSIZE;
		int currentIndexWriting = 6;
		for (Parameter parameter : parameterList) {
			command_buffer[currentIndexWriting] = parameter.getParameterLength();
			currentIndexWriting++;
			System.arraycopy(parameter.getParameterData(), 0, command_buffer, currentIndexWriting, parameter.getParameterLength());
			currentIndexWriting+=parameter.getParameterLength();
		}
		
		byte CRC = CRC8_CCITT.crc_CCITT(command_buffer, currentIndexWriting);
		int footerStartIndex = currentIndexWriting;
		
		command_buffer[footerStartIndex] = SectionDividersChar.ETX.getCode();
		command_buffer[footerStartIndex+1] = CRC;
		command_buffer[footerStartIndex+2] = SectionDividersChar.ETB.getCode();
		return CommandStatus.SUCCESSFUL_COMPOSITION;
	}
	
	
	public static CommandStatus DecomposeMessageFromBuffer() {
		try {
			String commandString = new String(command_buffer, "ISO-8859-1");
			Pattern headerPattern = Pattern.compile("\\u0001(.{3}?)\\u0002", Pattern.DOTALL);
			Pattern footerPattern = Pattern.compile("\\u0003(.{1}?)\\u0017", Pattern.DOTALL);
			Matcher headerMatcher = headerPattern.matcher(commandString);
			Matcher footerMatcher = footerPattern.matcher(commandString);
			
			if (headerMatcher.find()==false) return CommandStatus.WRONG_HEADER_SEGMENTATION;
			if (footerMatcher.find()==false) return CommandStatus.WRONG_FOOTER_SEGMENTATION;
			
			int headerStartIndex = headerMatcher.start();
			int parameterStartIndex = headerMatcher.end()+1;
			int footerStartIndex = footerMatcher.start();
			
			lastMessagePID = command_buffer[headerStartIndex+1];	//verificar si hay que convertir a desde unsigned
			if (command_buffer[headerStartIndex+2]!=currentModuleID.getInternalCode()) return CommandStatus.WRONG_MODULE_ID;
			
			lastMessageTransmitterModule = Module.getModule(command_buffer[headerStartIndex+3]);
			
			lastCommandType = CommandType.definedCommandList.stream().filter(p-> p.getCommandCode()==command_buffer[headerMatcher.end()]).findFirst().orElse(null);
			
			lastParameterList = new ArrayList<Parameter>();
			
			while(parameterStartIndex!=footerStartIndex) {
				int parameterLength = command_buffer[parameterStartIndex];
				byte[] parameterData = new byte[parameterLength];
				System.arraycopy(command_buffer, parameterStartIndex+1, parameterData, 0, parameterLength);
				lastParameterList.add(new Parameter(parameterData));
				parameterStartIndex+=(1+parameterLength);
			}

			if (command_buffer[footerStartIndex+1]!=CRC8_CCITT.crc_CCITT(command_buffer, footerStartIndex)) return CommandStatus.WRONG_CHECKSUM_CONSISTENCY;
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return CommandStatus.FAILED_DECOMPOSITION;
		}
		
		
		isAvailableCommand = true;
		return CommandStatus.SUCCESSFUL_DECOMPOSITION;
	}
	
	public static void handleReceivedCommand() {
		if (isAvailableCommand) {
			if (lastCommandType!=null) lastCommandType.handleCommand();
			isAvailableCommand = false;
		}
	}
	
	
	enum CommandStatus {
		SUCCESSFUL_DECOMPOSITION((byte)0x00),
		SUCCESSFUL_COMPOSITION((byte)0x01),
		WRONG_HEADER_SEGMENTATION((byte)0x02),	
		WRONG_FOOTER_SEGMENTATION((byte)0x03),	
		WRONG_CHECKSUM_CONSISTENCY((byte)0x04),	
		WRONG_MODULE_ID((byte)0x05),			
		UNDEFINED_COMMAND_CODE((byte)0x06),		
		PARAMETER_DATA_OVERFLOW((byte)0x07),	
		PARAMETER_COUNT_OVERSIZE((byte)0x08),	
		FAILED_DECOMPOSITION((byte)0x09),
		FAILED_COMPOSITION((byte)0x0A);	

		private final byte code;
		CommandStatus(byte code){		
			this.code = code;
		}
		public byte getCode() {
		    return code;
		}
		
	}
	
	public static class Module extends UnifiedTransmissionProtocol{
		private String moduleName;
		private String moduleAlias;
		private byte internalCode;
		private int iconResourceId;
		private List<Device> devicesList;
		
		Module(String moduleName,String moduleAlias, byte internalCode, int iconResourceId, List<Device> devicesList){
			this.moduleName = moduleName;
			this.internalCode = internalCode;
			this.iconResourceId = iconResourceId;
			this.devicesList = devicesList;
		}
		
		Module(){
			
		}
		
		public List<Device> getDevicesList() {
			return devicesList;
		}
		
		public int getIconResourceId() {
			return iconResourceId;
		}
		
		public byte getInternalCode() {
			return internalCode;
		}
		
		public String getModuleName() {
			return moduleName;
		}

		public String getModuleAlias() {
			return moduleAlias;
		}

		public List<Parameter> exportDataToParameterList(){
			//Desarrollar esta funcion
			// AVR-GCC USES LITTLE-ENDIAN
			// AVR-GCC USES STRICTLY IEEE 754 32-BIT FLOAT POINT
			
			List<Parameter> exportList = new ArrayList<UnifiedTransmissionProtocol.Parameter>();
			
			for (int x = 0; x < devicesList.size(); x++) {
				for (Module.Device device : devicesList) {
					if (x==byteToUnsignedInt(device.getInternalIndex())) {
						Parameter newParameter = null;
						
						if (device.getData().getClass()==Byte.class) {
							ByteBuffer byteBuffer = ByteBuffer.allocate(Byte.BYTES);
							byteBuffer.order(ByteOrder.LITTLE_ENDIAN); 
							byteBuffer.put((byte) device.getData());
							newParameter = new Parameter(byteBuffer.array());
						} else if (device.getData().getClass()==Short.class) {
							ByteBuffer shortBuffer = ByteBuffer.allocate(Short.BYTES);
							shortBuffer.order(ByteOrder.LITTLE_ENDIAN); 
							shortBuffer.putShort((short) device.getData());
							newParameter = new Parameter(shortBuffer.array());
						} else if (device.getData().getClass()==Integer.class) {
							ByteBuffer intBuffer = ByteBuffer.allocate(Integer.BYTES);
							intBuffer.order(ByteOrder.LITTLE_ENDIAN); 
							intBuffer.putInt((int) device.getData());
							newParameter = new Parameter(intBuffer.array());
						} else if (device.getData().getClass()==Float.class) {
							ByteBuffer floatBuffer = ByteBuffer.allocate(Float.BYTES);
							floatBuffer.order(ByteOrder.LITTLE_ENDIAN); 
							floatBuffer.putFloat((float) device.getData());
							newParameter = new Parameter(floatBuffer.array());
						} else if (device.getData().getClass()==Double.class) {
							ByteBuffer doubleBuffer = ByteBuffer.allocate(Float.BYTES);
							doubleBuffer.order(ByteOrder.LITTLE_ENDIAN); 
							doubleBuffer.putFloat((float)((double)device.getData()));
							newParameter = new Parameter(doubleBuffer.array());
						} else if (device.getData().getClass()==String.class) {
							String data = (String) device.getData();
							newParameter = new Parameter(data.getBytes(StandardCharsets.US_ASCII));
						} else if (device.getData().getClass()==Boolean.class) {
							byte data = (byte) 0x00;
							if ((Boolean)device.getData()==true) data = (byte)0xFF;
							ByteBuffer booleanBuffer = ByteBuffer.allocate(Byte.BYTES);
							booleanBuffer.order(ByteOrder.LITTLE_ENDIAN); 
							booleanBuffer.put(data);
							newParameter = new Parameter(booleanBuffer.array());
						}
						
						if (newParameter!=null) exportList.add(newParameter);
						
						break;
					}
					
				}
			}
			
			return exportList;
		}
		
		public void updateDataFromParameterList(List<Parameter> parameterList){
			
			for (int x = 0; x < parameterList.size(); x++) {
				Device deviceToUpdate = devicesList.get(x);
				ByteBuffer buffer = ByteBuffer.wrap(parameterList.get(x).getParameterData()); 
				buffer.order(ByteOrder.LITTLE_ENDIAN);
				
				if (deviceToUpdate.getData().getClass()==Byte.class) {
					deviceToUpdate.setData(new Byte(buffer.get()));
				} else if (deviceToUpdate.getData().getClass()==Short.class) {
					deviceToUpdate.setData(new Short(buffer.getShort()));
				} else if (deviceToUpdate.getData().getClass()==Integer.class) {
					deviceToUpdate.setData(new Integer(buffer.getInt()));
				} else if (deviceToUpdate.getData().getClass()==Float.class) {
					deviceToUpdate.setData(new Float(buffer.getFloat()));
				} else if (deviceToUpdate.getData().getClass()==Double.class) {
					deviceToUpdate.setData(new Double(buffer.getFloat()));
				} else if (deviceToUpdate.getData().getClass()==String.class) {
					deviceToUpdate.setData(new String(parameterList.get(x).getParameterData(),StandardCharsets.US_ASCII));
				} else if (deviceToUpdate.getData().getClass()==Boolean.class) {
					if (buffer.get()==(byte)0xFF) {
						deviceToUpdate.setData(new Boolean(true));	
					} else {
						deviceToUpdate.setData(new Boolean(false));	
					}
				}
				 
				
				devicesList.set( x, deviceToUpdate );
			}
		}
		
		public void updateDeviceValue(byte internalIndex, byte[] data) {
			for (int x = 0; x < devicesList.size(); x++) {
				if (devicesList.get(x).getInternalIndex()==internalIndex) {
					Device deviceToUpdate = devicesList.get(x);
					ByteBuffer buffer = ByteBuffer.wrap(data); 
					buffer.order(ByteOrder.LITTLE_ENDIAN);
					
					if (deviceToUpdate.getData().getClass()==Byte.class) {
						deviceToUpdate.setData(new Byte(buffer.get()));
					} else if (deviceToUpdate.getData().getClass()==Short.class) {
						deviceToUpdate.setData(new Short(buffer.getShort()));
					} else if (deviceToUpdate.getData().getClass()==Integer.class) {
						deviceToUpdate.setData(new Integer(buffer.getInt()));
					} else if (deviceToUpdate.getData().getClass()==Float.class) {
						deviceToUpdate.setData(new Float(buffer.getFloat()));
					} else if (deviceToUpdate.getData().getClass()==Double.class) {
						deviceToUpdate.setData(new Double(buffer.getFloat()));
					} else if (deviceToUpdate.getData().getClass()==String.class) {
						deviceToUpdate.setData(new String(data,StandardCharsets.US_ASCII));
					} else if (deviceToUpdate.getData().getClass()==Boolean.class) {
						if (buffer.get()==(byte)0xFF) {
							deviceToUpdate.setData(new Boolean(true));	
						} else {
							deviceToUpdate.setData(new Boolean(false));	
						}
					}
				}
			}
		}
		
		
		public byte[] exportDeviceValue(byte internalIndex) {
			for (int x = 0; x < devicesList.size(); x++) {
				if (devicesList.get(x).getInternalIndex()==internalIndex) {
					
					if (devicesList.get(x).getData().getClass()==Byte.class) {
						ByteBuffer byteBuffer = ByteBuffer.allocate(Byte.BYTES);
						byteBuffer.order(ByteOrder.LITTLE_ENDIAN); 
						byteBuffer.put((byte) devicesList.get(x).getData());
						return byteBuffer.array();
					} else if (devicesList.get(x).getData().getClass()==Short.class) {
						ByteBuffer shortBuffer = ByteBuffer.allocate(Short.BYTES);
						shortBuffer.order(ByteOrder.LITTLE_ENDIAN); 
						shortBuffer.putShort((short) devicesList.get(x).getData());
						return shortBuffer.array();
					} else if (devicesList.get(x).getData().getClass()==Integer.class) {
						ByteBuffer intBuffer = ByteBuffer.allocate(Integer.BYTES);
						intBuffer.order(ByteOrder.LITTLE_ENDIAN); 
						intBuffer.putInt((int) devicesList.get(x).getData());
						return intBuffer.array();
					} else if (devicesList.get(x).getData().getClass()==Float.class) {
						ByteBuffer floatBuffer = ByteBuffer.allocate(Float.BYTES);
						floatBuffer.order(ByteOrder.LITTLE_ENDIAN); 
						floatBuffer.putFloat((float) devicesList.get(x).getData());
						return floatBuffer.array();
					} else if (devicesList.get(x).getData().getClass()==Double.class) {
						ByteBuffer doubleBuffer = ByteBuffer.allocate(Float.BYTES);
						doubleBuffer.order(ByteOrder.LITTLE_ENDIAN); 
						doubleBuffer.putFloat((float)((double)devicesList.get(x).getData()));
						return doubleBuffer.array();
					} else if (devicesList.get(x).getData().getClass()==String.class) {
						String data = (String) devicesList.get(x).getData();
						return data.getBytes(StandardCharsets.US_ASCII);
					} else if (devicesList.get(x).getData().getClass()==Boolean.class) {
						byte data = (byte) 0x00;
						if ((Boolean)devicesList.get(x).getData()==true) data = (byte)0xFF;
						ByteBuffer booleanBuffer = ByteBuffer.allocate(Byte.BYTES);
						booleanBuffer.order(ByteOrder.LITTLE_ENDIAN); 
						booleanBuffer.put(data);
						return booleanBuffer.array();
					}
				}
			}
			
			return null;
		}


		private static Executor synchronizationExecutor = Executors.newSingleThreadExecutor();
		private static boolean synchronizationRunning = false;

		public static boolean awaitingModuleResponse = false;
		public static byte awaitingModuleResponse_ModuleInternalCode = (byte)0x00;
		public static boolean awaitingSynchronization = false;

		public static void syncAllDevicesValueToSystem(){
			// Send data to system from internal data


			if (synchronizationRunning==false) {
				synchronizationExecutor.execute(new Runnable() {
					@Override
					public void run() {
						synchronizationRunning = true;
						do {
							awaitingSynchronization=false;
							for (Module mod : modulesList) {
								for (Module.Device dev : mod.getDevicesList()) {
									if (dev.isSynchronizableOnlyFromSystem() == false && dev.isSynchronizedData() == false) {
										List<Parameter> export = mod.exportDataToParameterList();

										ComposeMessageToBuffer(CommandType.UPDATE_ALL_DEVICES_VALUE, mod, export);
										DeveloperConsoleActivity.sendTextToTerminal("Sent to: " + mod.getModuleName() + " : " + TerminalManager.insertSpaces(bytesToHex(command_buffer), " ", 2));
										awaitingModuleResponse = true;
										awaitingModuleResponse_ModuleInternalCode = mod.internalCode;
										BluetoothConnection.sendCommandBuffer(command_buffer);
										long startTimeMillis = System.currentTimeMillis();
										while ((System.currentTimeMillis() - startTimeMillis) < 1000L) {
											if (awaitingModuleResponse == false) {
												for (Module.Device devSync : mod.getDevicesList()) {
													devSync.setSynchronizedState(true);
												}
												break;
											}
										}
										awaitingModuleResponse = false;
										break; //Only send one message per module
									}
								}
							}
						} while (awaitingSynchronization==true);
						synchronizationRunning = false;
					}
				});
			} else {
				awaitingSynchronization = true;
			}

		}

		public static void syncAllDevicesValueFromSystem(){
			// Retrieve data from system to internal data
			// Metodo estatico


			synchronizationRunning = true;

			synchronizationRunning = false;
		}


		
		
		public static Module getModule(byte internalCode) {
			for (Module mod : modulesList) {
				if (mod.getInternalCode()==internalCode) return mod;
			}
			return null;
		}
		
		public static Module getLocalModule() {
			byte localModuleInternalCode = (byte) 0x00;
			for (Module mod : modulesList) {
				if (mod.getInternalCode()==localModuleInternalCode) return mod;
			}
			return null;
		}
		
		public String getModuleStatusTerminal() {
			
			String composedString = new String("");
			

			
			for (Device dev : devicesList) {
				composedString += dev.getName() + " : " + dev.getData().toString() + System.lineSeparator();
			}

		

			return composedString;
		}
		
		
		public static class Device{
			private String name;
			public Object data;
			private int iconResourceId;
			private byte internalIndex; 	// Index or internal code of the device
			private boolean synchronizedData = false;
			private boolean synchronizableOnlyFromSystem = false;	// Only synchronize from system
			
			Device(String name, Object data, int iconResourceId, byte internalIndex, boolean synchronizableOnlyFromSystem){
				this.name = name;
				this.data = data;
				this.iconResourceId = iconResourceId;
				this.internalIndex = internalIndex;
				this.synchronizableOnlyFromSystem = synchronizableOnlyFromSystem;
			}
			
			public void setData(Object data) {
				if (synchronizableOnlyFromSystem == false) {
					CommandsActivity.updateGUI();
					this.data = data;
					synchronizedData = false;
				}
			}
			
			public Object getData() {
				return data;
			}
			
			public int getIconResourceId() {
				return iconResourceId;
			}
			
			public byte getInternalIndex() {
				return internalIndex;
			}
			
			public String getName() {
				return name;
			}

			public boolean isSynchronizableOnlyFromSystem() {
				return synchronizableOnlyFromSystem;
			}

			public boolean isSynchronizedData() {
				return synchronizedData;
			}

			public void setSynchronizedState(boolean synchronizedData) {
				this.synchronizedData = synchronizedData;
			}

			public void deviceOnClickHandler(){
				switch (name){
					case "Módulo principal":
						break;
					case "Módulo de potencia":
						break;
					case "Módulo motriz":
						break;

					case "Velador":
						if ((boolean)data == false) { setData(Boolean.TRUE); } else { setData(Boolean.FALSE); }
						break;
					case "Luz":
						if ((boolean)data == false) { setData(Boolean.TRUE); } else { setData(Boolean.FALSE); }
						break;
					case "Ventilador":
						if ((boolean)data == false) { setData(Boolean.TRUE); } else { setData(Boolean.FALSE); }
						break;
					case "Estufa":
						if ((boolean)data == false) { setData(Boolean.TRUE); } else { setData(Boolean.FALSE); }
						break;
					case "Camilla":
						if (byteToUnsignedInt((byte)data)<3) { setData((byte)(((byte)data)+1)); } else { setData((byte)0x00); }
						break;
					case "Cortina":
						if (byteToUnsignedInt((byte)data)<7) { setData((byte)(((byte)data)+1)); } else { setData((byte)0x00); }
						break;
					case "Llamar enfermera":
						if ((boolean)data == false) {
							setData(Boolean.TRUE);

							autoToggleNurseStateThread = new Thread(autoToggleNurseStateRunnable);
							autoToggleNurseStateThread.start();
						} else { setData(Boolean.FALSE); }
						break;
				}
				syncAllDevicesValueToSystem();
			}

			public int getProgressBarValue(){
				int returnValue = 0;
				switch (name){
					case "Módulo principal":
						if ((boolean)data == false) { returnValue = 0; } else { returnValue = 100; }
						break;
					case "Módulo de potencia":
						if ((boolean)data == false) { returnValue = 0; } else { returnValue = 100; }
						break;
					case "Módulo motriz":
						if ((boolean)data == false) { returnValue = 0; } else { returnValue = 100; }
						break;

					case "Velador":
						if ((boolean)data == false) { returnValue = 0; } else { returnValue = 100; }
						break;
					case "Luz":
						if ((boolean)data == false) { returnValue = 0; } else { returnValue = 100; }
						break;
					case "Ventilador":
						if ((boolean)data == false) { returnValue = 0; } else { returnValue = 100; }
						break;
					case "Estufa":
						if ((boolean)data == false) { returnValue = 0; } else { returnValue = 100; }
						break;

					case "Camilla":
						returnValue = Math.round((float)(((byte)data)*33.333f));
						break;
					case "Cortina":
						returnValue = Math.round((float)(((byte)data)*14.286f));;
						break;
					case "Llamar enfermera":
						if ((boolean)data == false) { returnValue = 0; } else { returnValue = 100; }
						break;
				}
				return returnValue;
			}

			public String getDataAsString(){
				String returnString = "";
				switch (name){
					case "Módulo principal":
						if ((boolean)data == false) { returnString = "Desconectado"; } else { returnString = "En linea"; }
						break;
					case "Módulo de potencia":
						if ((boolean)data == false) { returnString = "Desconectado"; } else { returnString = "En linea"; }
						break;
					case "Módulo motriz":
						if ((boolean)data == false) { returnString = "Desconectado"; } else { returnString = "En linea"; }
						break;

					case "Velador":
						if ((boolean)data == false) { returnString = "Apagado"; } else { returnString = "Encendido"; }
						break;
					case "Luz":
						if ((boolean)data == false) { returnString = "Apagado"; } else { returnString = "Encendido"; }
						break;
					case "Ventilador":
						if ((boolean)data == false) { returnString = "Apagado"; } else { returnString = "Encendido"; }
						break;
					case "Estufa":
						if ((boolean)data == false) { returnString = "Apagado"; } else { returnString = "Encendido"; }
						break;

					case "Camilla":
						returnString = "Posición " + byteToUnsignedInt((byte)data);
						break;
					case "Cortina":
						returnString = "Posición " + byteToUnsignedInt((byte)data);
						break;
					case "Llamar enfermera":
						if ((boolean)data == false) { returnString = "Disponible"; } else { returnString = "Llamando"; }
						break;
				}
				return returnString;
			}

			public static Thread autoToggleNurseStateThread;
			public static Runnable autoToggleNurseStateRunnable = new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(3000);
						modulesList.get(3).getDevicesList().get(2).setData(new Boolean(false));
						modulesList.get(3).getDevicesList().get(2).setSynchronizedState(true);
						CommandsActivity.updateGUI();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};

		}
		
	}
	
	enum SectionDividersChar {	
		SOH((byte)0x01),	
		STX((byte)0x02),	
		ETX((byte)0x03),
		ETB((byte)0x17);	

		private final byte code;
		SectionDividersChar(byte code){		
			this.code = code;
		}
		public byte getCode() {
		    return code;
		}
	}
	
	public static class CommandType{
		private byte commandCode;
		private String commandTag;
		private Runnable handlerRunnable;

		
		CommandType(byte cmdCode, String tag, Runnable handlerCommand){
			this.commandCode = cmdCode;
			this.commandTag = tag;
			this.handlerRunnable = handlerCommand;
		}
		
		public byte getCommandCode() {
			return commandCode;
		}
		
		public String getCommandTag() {
			return commandTag;
		}
		
		public void setCommandCode(byte commandCode) {
			this.commandCode = commandCode;
		}
		
		public void setCommandTag(String commandTag) {
			this.commandTag = commandTag;
		}
		
		public void handleCommand() {
			handlerRunnable.run();
		}


		public static ArrayList<CommandType> definedCommandList = new ArrayList<CommandType>(Arrays.asList(
				new CommandType((byte)0x00, "UPDATE_ALL_DEVICES_VALUE", new Runnable() {
					@Override
					public void run() {

						modulesList.get(byteToUnsignedInt(lastMessageTransmitterModule.getInternalCode())).updateDataFromParameterList(lastParameterList);

						System.out.println("Running UPDATE_ALL_DEVICES_VALUE!");

					}}),
				new CommandType((byte)0x01, "UPDATE_DEVICE_VALUE", new Runnable() {
					@Override
					public void run() {


						ByteBuffer buffer = ByteBuffer.wrap(lastParameterList.get(0).getParameterData());

						modulesList.get(byteToUnsignedInt(lastMessageTransmitterModule.getInternalCode())).updateDeviceValue(buffer.get(),lastParameterList.get(1).getParameterData());

						System.out.println("Running UPDATE_DEVICE_VALUE!");



					}}),
				new CommandType((byte)0x02, "GET_ALL_DEVICES_VALUE", new Runnable() {
					@Override
					public void run() {
						ComposeMessageToBuffer(definedCommandList.stream().filter(p-> p.getCommandTag().equals(("UPDATE_ALL_DEVICES_VALUE"))).findFirst().orElse(null),
								lastMessageTransmitterModule,
								modulesList.get(byteToUnsignedInt(lastMessageTransmitterModule.getInternalCode())).exportDataToParameterList());

						// SEND COMMAND_BUFFER VIA BLUETOOTH


						System.out.println("Running GET_ALL_DEVICES_VALUE!");
					}}),
				new CommandType((byte)0x03, "GET_DEVICE_VALUE", new Runnable() {
					@Override
					public void run() {

						ByteBuffer buffer = ByteBuffer.wrap(lastParameterList.get(0).getParameterData());
						byte[] deviceData = modulesList.get(byteToUnsignedInt(lastMessageTransmitterModule.getInternalCode())).exportDeviceValue(buffer.get());

						ComposeMessageToBuffer(definedCommandList.stream().filter(p-> p.getCommandTag().equals(("UPDATE_DEVICE_VALUE"))).findFirst().orElse(null),
								lastMessageTransmitterModule,
								new ArrayList<Parameter>(Arrays.asList(new Parameter(new byte[] {buffer.get()}), new Parameter(deviceData))));

						// SEND COMMAND_BUFFER VIA BLUETOOTH

						System.out.println("Running GET_DEVICE_VALUE!");
					}}),
				new CommandType((byte)0x04, "MESSAGE_STATUS", new Runnable() {
					@Override
					public void run() {

						ByteBuffer buffer = ByteBuffer.wrap(lastParameterList.get(0).getParameterData());
						if (buffer.get()==(byte)0x09){
							Module.awaitingModuleResponse = false;
						}




					}})
		));

		public static CommandType UPDATE_ALL_DEVICES_VALUE = definedCommandList.get(0);
		public static CommandType UPDATE_DEVICE_VALUE = definedCommandList.get(1);
		public static CommandType GET_ALL_DEVICES_VALUE = definedCommandList.get(2);
		public static CommandType GET_DEVICE_VALUE = definedCommandList.get(3);
		public static CommandType MESSAGE_STATUS = definedCommandList.get(4);

	}

	public static int byteToUnsignedInt(byte x){
		return ((int) x) & 0xff;
	}


	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}

	
	
	
}
