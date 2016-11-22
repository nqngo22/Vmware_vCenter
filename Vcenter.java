package nguyen.ngo;

import java.net.MalformedURLException;
import java.net.URL;
import com.vmware.vim25.*;
import com.vmware.vim25.mo.*;

import java.util.Date;
import java.util.Scanner;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class Vcenter {
	ServiceInstance si;
	
	private static void printHelp(){
		System.out.println("usage:\n"
				+ "exit\t\t\tExit the program\n"
				+ "help\t\t\tPrint out the usage, e.g., the entire list of commands\n"
				+ "host\t\t\tEnumerate all host\n"
				+ "host hname info\t\tShow info of host hname, e.g., host 188.88.88.88 info\n"
				+ "host hname datastore\tEnumerate datastores of host hname, e.g., host 188.88.88.88 datastore\n"
				+ "host hname network\tEnumerate datastores of host hnames, e.g., host 188.88.88.88 network\n"
				+ "vm\t\t\tEnumerate all virtual machines\n"
				+ "vm vname into\t\tShow info of VM vname, e.g., vm demo-centos7-123 info\n"
				+ "vm vname on\t\tPower on VM vname and wait until task completes, e.g., vmdemo-centos7-123 on\n"
				+ "vm vname off\t\tPower off VM vname and wait until task completes, e.g., vm demo-centos7-123 off\n"
				+ "vn vname shutdown\tShutdown guest of VM vname, e.g., vm demo-centos7-123 shutdown");
	}
	
	private void host(){
		Folder rootFolder = si.getRootFolder();
		try{
			ManagedEntity[] mes = new InventoryNavigator(rootFolder).searchManagedEntities("HostSystem");	
			for(int i = 0; i < mes.length; i++){
				HostSystem hs = (HostSystem)mes[i];
				System.out.println("host[" + i + "]: Name = " + hs.getName());
			}
		}catch(RemoteException e){
			System.err.println("RemoteException: " + e.getMessage());
		}
	}
	
	private void hostInfo(String hname){
		Folder rootFolder = si.getRootFolder();
		try{
			HostSystem hs = (HostSystem) new InventoryNavigator(rootFolder).searchManagedEntity("HostSystem", hname);
			if(hs != null){
				int mem_size = (int)(hs.getHardware().getMemorySize() / (int)(Math.pow(2, 30)));
				int cpu_cores = hs.getHardware().getCpuInfo().getNumCpuCores();
				//String full_name = si.getAboutInfo().getFullName();
				String full_name = hs.getConfig().getProduct().getFullName();
				System.out.println("host: ");
				System.out.println("\tName = " + hs.getName());
				System.out.println("\tProductFullName = " + full_name);
				System.out.println("\tCpu cores = " + cpu_cores);
				System.out.println("\tRAM = " + mem_size + " GB");
			}else{
				System.out.println("invalid hostname = " + hname);
			}
		}catch(RemoteException e){
			System.err.println("RemoteException: " + e.getMessage());
		}
	}
	
	private void hostNetworks(String hname){
		Folder rootFolder = si.getRootFolder();
		try{
			HostSystem hs = (HostSystem) new InventoryNavigator(rootFolder).searchManagedEntity("HostSystem", hname);
			if(hs != null){
				System.out.println("host: ");
				System.out.println("\tName = " + hs.getName());
				Network[] nws = hs.getNetworks();
				for(int i = 0; i < nws.length; i++){
					System.out.println("\tNetwork[" + i + "]: name=" + nws[i].getName());
				}
			}else{
				System.err.println("invalid hostname = " + hname);
			}
		}catch(RemoteException e){
			System.err.println("RemoteException: " + e.getMessage());
		}
	}
	
	private void hostDataStores(String hname){
		Folder rootFolder = si.getRootFolder();
		try{
			HostSystem hs = (HostSystem) new InventoryNavigator(rootFolder).searchManagedEntity("HostSystem", hname);
			if(hs != null){
				System.out.println("host: ");
				System.out.println("\tName = " + hs.getName());
				Datastore[] dss = hs.getDatastores();
				for(int i = 0; i < dss.length; i++){
					int free_space = (int)(dss[i].getSummary().getFreeSpace() / (int)(Math.pow(2, 30)));
					int capacity = (int)(dss[i].getSummary().getCapacity() / (int)(Math.pow(2, 30)));
					System.out.println("\tDatastore[" + i + "] " + "name=" + dss[i].getName() + ", capacity = " + capacity + " GB, "
					+ "FreeSpace = "+ free_space + " GB.");
				}	
			}else{
				System.err.println("invalid hostname = " + hname);
			}
		}catch(RemoteException e){
			System.err.println("RemoteException: " + e.getMessage());
		}
	}
	
	private void vm(){
		Folder rootFolder = si.getRootFolder();
		try{
			ManagedEntity[] mes = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
			for(int i = 0; i < mes.length; i++){
				VirtualMachine vm = (VirtualMachine)mes[i];
				System.out.println("vm[" + i + "]: Name = " + vm.getName());
			}
		}catch(RemoteException e){
			System.err.println("RemoteException: " + e.getMessage());
		}
	}
	
	private void vmInfo(String vname){
		Folder rootFolder = si.getRootFolder();
		try{
			VirtualMachine vm = (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine", vname);
			if(vm != null){
				System.out.println("vm:");
				System.out.println("\tName = " + vm.getName());
				System.out.println("\tGuest full name = " + vm.getGuest().getGuestFullName());
				System.out.println("\tGuest state = " + vm.getGuest().getGuestState());
				System.out.println("\tIP addr = " + vm.getGuest().getIpAddress());
				System.out.println("\tTool running status = " + vm.getGuest().getToolsRunningStatus());
				System.out.println("\tPower state = " + vm.getRuntime().getPowerState());
			}else{
				System.out.println("invalid vm = " + vname);
			}
		}catch(RemoteException e){
			System.err.println("RemoteException: " + e.getMessage());
		}
	}
	
	private void vmOn(String vname){
		Folder rootFolder = si.getRootFolder();
		try{
			VirtualMachine vm = (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine", vname);
			if(vm != null){
				//if the vm exists
				System.out.println("vm: ");
				System.out.println("\tName = " + vm.getName());
				//if(vm.getRuntime().getPowerState().toString().equals("poweredOff")){
				//if vm is currently off
				Task task = vm.powerOnVM_Task(null); // argument = null : vm is started on the currently associated host	
				try{
					String task_result = task.waitForTask();
					DateFormat date_format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
					String completion_time = date_format.format(task.getTaskInfo().getCompleteTime().getTime()).toString();
					if(task_result.equals("success")){
						System.out.println("\tPower on VM: status " + task.getTaskInfo().getState().toString()
							+ ", completion time = " + completion_time);
					}else{
						//vm is powered on?
						System.out.println("\tPower on VM: status = The attempted operation cannot be performed in the current state (Powered on)., completion time = " 
							+ completion_time);
					}
				}catch(InterruptedException e){
					System.err.println("InterruptedException: " + e.getMessage());
				}
			} else{
				//if the vm does not exist
				System.out.println("invalid vm = " + vname);
			}
		}catch(RemoteException e){
			System.err.println("RemoteException: " + e.getMessage());
		}
	}
	
	private void vmOff(String vname, boolean print_vm_name){
		Folder rootFolder = si.getRootFolder();
		try{
			VirtualMachine vm = (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine", vname);
			if(vm != null){
				//if the vm exists
				if(print_vm_name == true){
					//If calling from anywhere but vmShutDownGuest(), then print vm name
					System.out.println("vm: ");
					System.out.println("\tName = " + vm.getName());
				}
				//if vm is currently on
				Task task = vm.powerOffVM_Task();	
				try{
					String task_result = task.waitForTask();
					DateFormat date_format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
					String completion_time = date_format.format(task.getTaskInfo().getCompleteTime().getTime()).toString();
					if(task_result.equals("success")){
						System.out.println("\tPower off VM: status " + task.getTaskInfo().getState().toString()
							+ ", completion time = " + completion_time);
					}else{
						//vm is powered off?
						System.out.println("\tPower off VM: status = The attempted operation cannot be performed in the current state (Powered off)., completion time = " 
							+ completion_time);
					}
				}catch(InterruptedException e){
					System.err.println("InterruptedException: " + e.getMessage());
				}
			} else{
				//if the vm does not exist
				System.out.println("invalid vm = " + vname);
			}
		}catch(RemoteException e){
			System.err.println("RemoteException: " + e.getMessage());
		}
	}
	
	private void vmShutDownGuest(String vname){
		Folder rootFolder = si.getRootFolder();
		try{
			VirtualMachine vm = (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine", vname);
			if(vm != null){
				//if the vm exists
				System.out.println("vm: ");
				System.out.println("\tName = " + vm.getName());
				try{
					vm.shutdownGuest();
					//loop until vm finishes shutting down
					//Extra credit
					long start_time = System.currentTimeMillis();
					long wait_time = 1800000; //three minutes
					long end_time = start_time + wait_time;
					boolean time_limit_exceeded = false;
					
					while(!vm.getRuntime().getPowerState().toString().equals("poweredOff")){
						if(System.currentTimeMillis() > end_time){
							time_limit_exceeded = true;
							break;
						}
						try{
							Thread.sleep(2000);
						}catch(InterruptedException e){
							System.err.println(e.getMessage());
						}
					}
					if(time_limit_exceeded == false){
						DateFormat date_format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
						Date date = new Date();
						System.out.println("\tShutdown guest: completed time = " + date_format.format(date));
					}else{
						System.out.println("\tGraceful shutdown failed. Now try a hard power off");
						vmOff(vname, false);
					}
				}catch(RemoteException e){
					System.err.println("\tRemoteException e: " + e.getMessage());
					System.out.println("\tGraceful shutdown failed. Now try a hard power off");
					vmOff(vname, false);
				}
			}else{
				System.out.println("invalid vm = " + vname);
			}
		}catch(RemoteException e){
			System.err.println("RemoteException: " + e.getMessage());
		}
	}
	
	public void run(){
		Scanner scan = new Scanner(System.in);
		String s;
		do{
			System.out.print(">");
			s = scan.nextLine();
			String[] commands = s.split("\\s+");
			switch(commands[0]){
			case "help":
				printHelp();
				break;
			case "host":
				if(commands.length == 1){
					host();
				}else if(commands.length == 3){
					switch(commands[2]){
					case "info":
						hostInfo(commands[1]);
						break;
					case "datastore":
						hostDataStores(commands[1]);
						break;
					case "network":
						hostNetworks(commands[1]);
						break;
					default:
						break;
					}
				}
				break;
			case "vm":
				if(commands.length == 1){
					vm();
				}else if(commands.length == 3){
					switch(commands[2]){
					case "info":
						vmInfo(commands[1]);
						break;
					case "on":
						vmOn(commands[1]);
						break;
					case "off":
						vmOff(commands[1], true);
						break;
					case "shutdown":
						vmShutDownGuest(commands[1]);
						break;
					default:
						break;
					}
				}
				break;
			default:
				break;
			}
			
		}while(!s.equals("exit"));
		scan.close();
		si.getServerConnection().logout();
	}
	
	public Vijava_App(String IP, String login, String password){
		//System.out.println(IP + " " + login + " " + password);
		try{
			si = new ServiceInstance(new URL(IP), login, password, true);
		} catch (RemoteException e){
			System.err.println("RemoteException: " + e.getMessage());
			System.exit(1);
		} catch (MalformedURLException e){
			System.err.println("MalformedURLException: " + e.getMessage());
			System.exit(1);
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		if(args.length < 3){
			System.out.println("Usage: Vcenter <IP> <login> <password>");
			return;
		}
		for(String s: args){
			System.out.println(s);
		}
		
		Vijava_App vijava_app = new Vijava_App(args[0], args[1], args[2]);
		vijava_app.run();
	}
}
