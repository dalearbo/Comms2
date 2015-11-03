package org.ros.android.jaguar;

interface RaspberryServiceReporter {
	void receivedMessageFromSocket(in byte[] inMessageFromSocket);
}