package com.vmware.g11n;
/**
* Used to deploy a virtual machine of ova by ovftool.
*
* Created by dni@vmware.com on 5/8/15
*/


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class OVFDeployer {

	private final String ovfTool = "./OVFTool/Windows/ovftool.exe";
	//options for vm initialization
	private String vCenterVmDeployOptions = "vcenter.vm.deploy.options = --prop:guestinfo.cis.appliance.ssh.enabled=True --prop:guestinfo.cis.appliance.net.mode=static --prop:guestinfo.cis.appliance.net.addr.family=ipv4 --prop:vami.hostname.VMware_vCenter_Log_Insight=<hostname> --prop:vami.ip0.VMware_vCenter_Log_Insight=<IP> --prop:vami.netmask0.VMware_vCenter_Log_Insight=<subnet mask> --prop:vami.gateway.VMware_vCenter_Log_Insight=<IP> --prop:vami.DNS.VMware_vCenter_Log_Insight=10.117.0.1 --prop:vm.rootpw=<password> --acceptAllEulas --powerOffTarget --overwrite --powerOn";
	//vm package of ova
	private String vCenterVmDeploySource = "./TestDownload/VMware-vRealize-Log-Insight-3.1.0.ova";
	//management object referred by id
	private String vCenterVmDeployDestination = "vi://<user>:<password>@<IP of vCenter>?moref=vim.ResourcePool:resgroup-148";


	public int Deploy() {
		Process process = null;

		String cmdline = ovfTool + " " + vCenterVmDeployOptions + " " + vCenterVmDeploySource + " "
				+ vCenterVmDeployDestination;
		int exit = -1;
		Runtime rt = Runtime.getRuntime();
		try {
			process = rt.exec(cmdline);
			final InputStream is1 = process.getInputStream();
			final InputStream is2 = process.getErrorStream();

			//get normal I/O messages
			new Thread() {
				public void run() {
					BufferedReader br = new BufferedReader(new InputStreamReader(is1));
					try {
						String lineB = null;

						while ((lineB = br.readLine()) != null) {
							if (lineB != null)
								System.out.println(lineB);

						}
					} catch (IOException e) {
						System.out.println(e.getMessage());
					}
				}
			}.start();

			//get error messages
			new Thread() {
				public void run() {

					BufferedReader br2 = new BufferedReader(new InputStreamReader(is2));
					try {

						String lineC = null;
						while ((lineC = br2.readLine()) != null) {

							if (lineC != null)
								System.out.println(lineC);
						}
					} catch (IOException e) {
						System.out.println(e.getMessage());
					}
				}
			}.start();
			exit = process.waitFor();
			if (exit != 0) {
				return exit;
			} else {
				return 0;
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.out.println("failed to read output from process" + e);
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
		return exit;

	}

}
