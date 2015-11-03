package org.ros.android.jaguar;

import org.ros.android.jaguar.RaspberryServiceReporter;

interface RaspberryService {
	void add(RaspberryServiceReporter reporter);
	void remove(RaspberryServiceReporter reporter);
	void sendMessageToSocket(in byte[] outMessageToSocket);
}