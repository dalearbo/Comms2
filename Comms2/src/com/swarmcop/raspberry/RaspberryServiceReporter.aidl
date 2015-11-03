package com.swarmcop.raspberry;

interface RaspberryServiceReporter {
	void receivedMessageFromSocket(in byte[] inMessageFromSocket);
}