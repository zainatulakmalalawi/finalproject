package com.G2.SNMP.Server;

import java.io.File;
import java.io.IOException;

import org.snmp4j.TransportMapping;
import org.snmp4j.agent.BaseAgent;
import org.snmp4j.agent.CommandProcessor;
import org.snmp4j.agent.DuplicateRegistrationException;
import org.snmp4j.agent.MOGroup;
import org.snmp4j.agent.ManagedObject;
import org.snmp4j.agent.mo.MOTableRow;
import org.snmp4j.agent.mo.snmp.RowStatus;
import org.snmp4j.agent.mo.snmp.SnmpCommunityMIB;
import org.snmp4j.agent.mo.snmp.SnmpNotificationMIB;
import org.snmp4j.agent.mo.snmp.SnmpTargetMIB;
import org.snmp4j.agent.mo.snmp.StorageType;
import org.snmp4j.agent.mo.snmp.VacmMIB;
import org.snmp4j.agent.security.MutableVACM;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModel;
import org.snmp4j.security.USM;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Interger32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;
import org.snmp4j.transport.TransportMappings;

public class SNMPAgent extends BaseAgent {

	private String address;

	public SNMPAgent(String address) throws IOException {

		super(new File("conf.agent"), new File("bootCounter.agent"), 
				new CommandProcessor( new
					OctetString(MPv3.createLocal.EngineId())));
		this.address = address;
	}

protected void addCommunities(SnmpCommunityMIB communityMIB ){

Variable[] com2sec = new Variable[] { new
OctetString("public"),
          new OctetString("cpublic"),
          getAgent().getContextEngineID(),
          new OctetString("public"),
          new OctetString(),
          new Interger32(StorageType.nonVolatile),
          new Interger32(RowStatus.active)
};

        MOTable row =
	communityMIB.getSnmpCommunityEntry().createRow(
			new OctetString("public2public").toSubIndex(true),com2sec);
	communityMIB.getSnmpCommunityEntry().addRow(row);
}

protected void addNotificationTargets(SnmpTargetMIB arg0, 
		SnmpNotificationMIB arg1) {
}


protected void addUsmUser(USM arg0) {

}

protected void addViews(VacmMIB vacm) {
	vacm.addGroup(SecurityModel.SECURITY_MODEL_SNMPv2c ,
			new OctetString ("cpublic"), 
			new OctetString ("v1v2group"),
			StorageType.nonVolatile);

	vacm.addAccess(new OctetString("v1v2group"),
			new OctetString("public"),
			SecurityModel.SECURITY_MODEL_ANY,
			SecurityLevel.NOAUTH_NOPRIV,
			MutableVACM.VACM_MATCH_EXACT,
			new OctetString("fullReadView"),
			new OctetString("fullNotifyView),
			StorageType.nonVolatile);

	vacm.addviewTreeFamily(new OctetString("fullReadView), new OID("1.3"),
			new OctetString().VacmMIB.vacmViewIncluded,
			StorageType.nonVolatile);

}

protected void unregisterManagedObjects() {
}

protected void registerManagedObjects() {
}

protected void initTransportMappings() throws IOException {
	transportMappings = new TransportMapping[1];
	Address addr = GenericAddress.parse(address);
	TransportMapping tm = TransportMappings.getInstance().
		createTransportMapping(addr);
	transportMappings[0] = tm;
}

public void start() throws IOException {

	init();
	addShutdownHook();
	getServer().addContext(new OctetString("public"));
	finishInit();
	run();
	sendColdStartNotification();

}

public void registerManagedObject(ManagedObject mo) {
	try {
		server.register(mo, null);
	}
catch (DuplicateRegistrationException ex)
{
throw new RuntimeException(ex);
}
}

public void unregisterManagedObject(MOGroup moGroup) {
	moGroup.unregisterMOs(server,getContext(moGroup));
}
}

package com.G2.SNMP.Server;

import org.snmp4j.agent.mo.MOAccessImpl;
import org.snmp4j.agent.mo.MOScalar;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;

public class MOCreator {
	public static MOScalar(oid,MOAccessImpl.ACCESS_READ_ONLY,
			getVariable(value));
}

private static Variable getVariable(Object value) {
	if(value instanceof String ) {
		return new OctetString((String)value);
	}
	throw new IllegalArgumentException("Unmanaged Type : " + value.getClass());
}
}

package com.G2.SNMP.Server;

import java.io.IOException;
import org.snmp4j.smi.OID;
import com.G2.SNMP.client.SNMPManager;

public class TestSNMPAgent {
	static final OID sysDescr = new OID(".1.3.6.1.2.1.1.1.0");

	public static void main(String[] args) throws IOException {
		TestSNMPAgent client= new TestSNMPAgent("udp:192.168.92.128/161");
		client.init();
	}

	SNMPAgent agent = null;

	SNMPManager client = null;
	
	String address = null;

	public TestSNMPAgent (String add) {
		address = add ;
	}

	private void init() throws IOException {
		agent = new SNMPAgent("0.0.0.0/2001");
		agent.start();

		agent.unregisterManagedObject(agent.getSnmpv2MIB());

		agent.registerManagedObject(MOcreater.createReadOnly
				(sysDescr, "This is Description is set by AIN & ZAINATUL"));

		client = new SNMPManager("udp:192.168.246.2/2001");
		client.start();

		System.out.println(client.getAsString(sysDescr));
	}
}

