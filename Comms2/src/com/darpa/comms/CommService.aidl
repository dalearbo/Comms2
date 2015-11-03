package com.darpa.comms;

import com.darpa.comms.CommServiceReporter;

interface CommService {
	void add(CommServiceReporter reporter);
	void remove(CommServiceReporter reporter);
	void sendMessage(in byte[] outMessageBytes);
}