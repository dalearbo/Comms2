package com.swarmcop.raspberry;

interface RaspberryServiceReporter {
	void receivedMessageFromSocket(in String inMessageFromSocket);
}