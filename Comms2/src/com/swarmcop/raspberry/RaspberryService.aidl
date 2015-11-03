package com.swarmcop.raspberry;

import com.swarmcop.raspberry.RaspberryServiceReporter;

interface RaspberryService {
	void add(RaspberryServiceReporter reporter);
	void remove(RaspberryServiceReporter reporter);
	void sendMessageToSocket(in byte[] outMessageToSocket);
}