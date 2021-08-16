package org.cloudbus.cloudsim.examples;

/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

/**
 * A simple example showing how to create a data center with one host and run one cloudlet on it.
 * 演示如何使用一台主机创建一个数据中心并在其上运行一个cloudlet。
 * ContainerCloudSimExample1模拟了如何使用一台主机、一台VM、一个容器创建一个数据中心，并在其上运行一个云任务
 * ContainerCloudSimExample1为例，可将仿真流程分成三个阶段：初始化仿真环境，执行仿真，结束仿真，从这三个阶段对容器编程进行了解。
 */
public class CloudSimExample1 {
	/** The cloudlet list. */
	private static List<Cloudlet> cloudletList;
	/** The vmlist. */
	private static List<Vm> vmlist;

	/**
	 * Creates main() to run this example.
	 *
	 * @param args the args
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Log.printLine("Starting CloudSimExample1...");

		try {
			// First step: Initialize the CloudSim package. It should be called before creating any entities.
			//在创建任何实体类之前，必须初始化CloudSim包
			int num_user = 1; // number of cloud users
			Calendar calendar = Calendar.getInstance(); // Calendar whose fields have been initialized with the current date and time.
			boolean trace_flag = false; // trace events

			/* Comment Start - Dinesh Bhagwat
			 * Initialize the CloudSim library.  初始化CloudSim库
			 *
			 * init（）调用initCommonVariable（），initCommonVariable（）又调用initialize（）（这三个方法都在CloudSim.java中定义）
			 * init() invokes initCommonVariable() which in turn calls initialize() (all these 3 methods are defined in CloudSim.java).
			 *
			 * initialize（）创建两个集合-SimEntity对象（表示模拟实体的命名实体）的ArrayList和 LinkedHashMap（名为entitiesByName，表示相同仿真实体的LinkedHashMap），每个SimEntity的名称作为键。
			 * initialize() creates two collections - an ArrayList of SimEntity Objects (named entities which denote the simulation entities) and
			 * a LinkedHashMap (named entitiesByName which denote the LinkedHashMap of the same simulation entities), with name of every SimEntity as the key.
			 *
			 * initialize()创造两个队列：一个future的SimEvent队列 和 一个deferred的SimEvenr队列
			 * initialize() creates two queues - a Queue of SimEvents (future) and another Queue of SimEvents (deferred).
			 *
			 * initialize()创造一个谓词的 HaspMap ， 这些谓词用于为延迟队列选择特定事件
			 * initialize() creates a HashMap of of Predicates (with integers as keys) - these predicates are used to select a particular event from the deferred queue.
			 *
			 * initialize() 将模拟时钟设置为0，将running设置为flse
			 * initialize() sets the simulation clock to 0 and running (a boolean flag) to false.
			 *
			 * 一旦initialize（）返回（请注意，我们现在使用的是initCommonVariable（）方法），就会创建一个CloudSimShutDown（从SimEntity派生）实例
			 * Once initialize() returns (note that we are in method initCommonVariable() now), a CloudSimShutDown (which is derived from SimEntity) instance is created
			 *
			 * （numuser为1，名称为CloudSimShutDown，id为-1，state为RUNNABLE）。然后将此新实体添加到模拟中
			 * (with numuser as 1, its name as CloudSimShutDown, id as -1, and state as RUNNABLE). Then this new entity is added to the simulation
			 *
			 * 当被添加到模拟中时，其id更改为0（从早期的-1）。两个集合-entities和entitiesByName使用此SimEntity更新。
			 * While being added to the simulation, its id changes to 0 (from the earlier -1). The two collections - entities and entitiesByName are updated with this SimEntity.
			 *
			 * shutdownId（其默认值为-1）为0
			 * the shutdownId (whose default value was -1) is 0
			 *
			 * 一旦initCommonVariable（）返回（请注意，我们现在使用的是init（）方法），就会创建一个CloudInformationService（它也是从SimEntity派生的）实例
			 *  Once initCommonVariable() returns (note that we are in method init() now), a CloudInformationService (which is also derived from SimEntity) instance is created
			 *
			 * （名称为CloudInformatinService，id为-1，状态为RUNNABLE）。然后这个新实体也被添加到模拟中。
			 *  (with its name as CloudInformatinService, id as -1, and state as RUNNABLE). Then this new entity is also added to the simulation.
			 *
			 * 当被添加到模拟中时，simentity的id从其早期的值-1更改为1（这是下一个id）。两个集合-entities和entitiesByName使用此SimEntity更新。CIID（其默认值为-1）为1
			 * While being added to the simulation, the id of the SimEntitiy is changed to 1 (which is the next id) from its earlier value of -1.
			 * The two collections - entities and entitiesByName are updated with this SimEntity.
			 * the cisId(whose default value is -1) is 1
			 * Comment End - Dinesh Bhagwat
			 */

//			第一步：对于容器，在创建一些实体之前，初始化CloudSim工具包。num_user（云用户数量）,calendar（日历）, trace_flag（标志位）。
			CloudSim.init(num_user, calendar, trace_flag);

//			第二步：创建数据中心   ，数据中心是cloudSim 的资源提供者、需要列出其中一个来运行CloudSim模拟
			// Second step: Create Datacenters
			// Datacenters are the resource providers in CloudSim. We need at
			// list one of them to run a CloudSim simulation
			Datacenter datacenter0 = createDatacenter("Datacenter_0");
//			第三步：创建代理
			// Third step: Create Broker
			DatacenterBroker broker = createBroker();
			int brokerId = broker.getId();
//			第四步:创建虚拟机
			// Fourth step: Create one virtual machine
			vmlist = new ArrayList<Vm>();

			// VM description 虚拟机描述
			int vmid = 0;
			int mips = 1000;
			long size = 10000; // image size (MB)
			int ram = 512; // vm memory (MB)
			long bw = 1000;
			int pesNumber = 1; // number of cpus
			String vmm = "Xen"; // VMM name

			// create VM
			Vm vm = new Vm(vmid, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());

			// add the VM to the vmList 将创建的vm添加到虚拟机队列
			vmlist.add(vm);

			// submit vm list to the broker 将创建的vm提交给代理
			broker.submitVmList(vmlist);
//			第四步:创建一个 cloudlet
			// Fifth step: Create one Cloudlet
			cloudletList = new ArrayList<Cloudlet>();

			// Cloudlet properties  cloudlet描述
			int id = 0;
			long length = 400000;
			long fileSize = 300;
			long outputSize = 300;
			UtilizationModel utilizationModel = new UtilizationModelFull();

			Cloudlet cloudlet =
					new Cloudlet(id, length, pesNumber, fileSize,
							outputSize, utilizationModel, utilizationModel,
							utilizationModel);
			cloudlet.setUserId(brokerId);
			cloudlet.setVmId(vmid);

			// add the cloudlet to the list  将cloudlet加入列表
			cloudletList.add(cloudlet);

			// submit cloudlet list to the broker 将cloudlet列表提交给代理
			broker.submitCloudletList(cloudletList);
//			第六步：开始模拟
			// Sixth step: Starts the simulation
			CloudSim.startSimulation();

			CloudSim.stopSimulation();

//			最后一步：打印结束信息
			//Final step: Print results when simulation is over
			List<Cloudlet> newList = broker.getCloudletReceivedList();
			System.out.println("打印CloudletList：");
			printCloudletList(newList);

			Log.printLine("CloudSimExample1 finished!");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("Unwanted errors happen");
		}
	}

	/**
	 * Creates the datacenter.
	 *
	 * @param name the name
	 *
	 * @return the datacenter
	 */
	private static Datacenter createDatacenter(String name) {

		//创建 PowerDatacenter 的步骤
		// Here are the steps needed to create a PowerDatacenter:
		// 1. We need to create a list to store  创建一个存储列表，来存储机器
		// our machine
		List<Host> hostList = new ArrayList<Host>();

		// 2. A Machine contains one or more PEs or CPUs/Cores.一台机器包含一个或多个PE或CPU
		// In this example, it will have only one core.
		List<Pe> peList = new ArrayList<Pe>();

		int mips = 1000;

		// 3. Create PEs and add these into a list. 创建PE并加入到列表中
		peList.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating 需要存储Pe id和MIPS等级（此处只加了一个pe）

		// 4. Create Host with its id and list of PEs and add them to the list 创建具有id和PE列表的主机，并将他们添加到计算机列表中
		// of machines
		int hostId = 0;
		int ram = 2048; // host memory (MB)
		long storage = 1000000; // host storage
		int bw = 10000;

		hostList.add(
				new Host(
						hostId,
						new RamProvisionerSimple(ram), //设置内存和带宽
						new BwProvisionerSimple(bw),
						storage,
						peList,
						new VmSchedulerTimeShared(peList)
				)
		); // This is our machine
		//创建一个 DatacenterCharacteristics 对象来存储数据中心的属性：体系结构、操作系统、计算机列表、分配策略（时间或空间共享、时区及其价格）
		// 5. Create a DatacenterCharacteristics object that stores the properties of a data center: architecture, OS, list of
		// Machines, allocation policy: time- or space-shared, time zone
		// and its price (G$/Pe time unit).
		String arch = "x86"; // system architecture  系统结构
		String os = "Linux"; // operating system  运行系统
		String vmm = "Xen";
		double time_zone = 10.0; // time zone this resource located  资源所在时区
		double cost = 3.0; // the cost of using processing in this resource 在此资源中实使用处理的成本
		double costPerMem = 0.05; // the cost of using memory in this resource 使用的内存资源消耗
		double costPerStorage = 0.001; // the cost of using storage in this resource 在此资源中使用存储的成本
		double costPerBw = 0.0; // the cost of using bw in this resource   在此资源中使用带宽的成本
		LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are not adding SAN devices by now

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
				arch, os, vmm, hostList, time_zone, cost, costPerMem,
				costPerStorage, costPerBw);

		// 6. Finally, we need to create a PowerDatacenter object.
		Datacenter datacenter = null;
		try {
			datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return datacenter;
	}

	// We strongly encourage users to develop their own broker policies, to submit vms and cloudlets according to the specific rules of the simulated scenario
//	我们强烈鼓励用户开发自己的代理策略，根据模拟场景的特定规则提交vm和cloudlet
//	此处，可以考虑如何改变代理策略
	/**
	 * Creates the broker.
	 *
	 * @return the datacenter broker
	 */
	private static DatacenterBroker createBroker() {
		DatacenterBroker broker = null;
		try {
			broker = new DatacenterBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}

	/**
	 * Prints the Cloudlet objects.
	 *
	 * @param list list of Cloudlets
	 */
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
				+ "Data center ID" + indent + "VM ID" + indent + "Time" + indent
				+ "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
				Log.print("SUCCESS");

				Log.printLine(indent + indent + cloudlet.getResourceId()
						+ indent + indent + indent + cloudlet.getVmId()
						+ indent + indent
						+ dft.format(cloudlet.getActualCPUTime()) + indent
						+ indent + dft.format(cloudlet.getExecStartTime())
						+ indent + indent
						+ dft.format(cloudlet.getFinishTime()));
			}
		}
	}
}
