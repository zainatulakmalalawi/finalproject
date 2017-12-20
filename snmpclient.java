package com.G2.SNMP.client;

import java.io.IOException;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SNMPManager {

	Snmp snmp = null;
	String address = null;

	public SNMPManager (String add)
	{
		address=add;
	}

	public static void main(String[] args) throws IOException {

		SNMPManager client = new SNMPManager("udp:192.168.92.128/161");
		client.start();

		String sysDescr = client.getAsString(new OID(".1.3.6.1.2.1.1.1.0"));
		System.out.println(sysDescr);
	}

	private void start() throws IOException {
		TransportMapping transport = new DefaultUdpTransportMapping();
		snmp= new Snmp(transport);

		transport.listen();
	}


	public String getAsString(OID oid) throws IOException {
		ResponseEvent event = get(new OID[] {oid});
		return event.getResponse().get(0).getVariable().toString();
	}

	public ResponseEvent get (OID oids[]) throws IOException {
		PDU pdu = new PDU ();
		for (OID oid : oids) {
			pdu.add(new VariableBinding(oid));
		}
		pdu.setType(PDU.GET);
		ResponseEvent event = snmp.send(pdu, getTarget(), null);
		if(event != null) {
			return event;
		}

		throw new RuntimeException("GET timed out");
	}


	private Target getTarget() {
		Address targetAddress = GenericAddress.parse(address);
		CommunityTarget target = new CommunityTarget();
		target.setCommunity (new OctetString("public"));
		target.setAddress(targetAddress);
		target.setRetries(2);
		target.setTimeout(1500);
		target.setVersion(SnmpConstants.version2c);
		return target ;
	}
}

