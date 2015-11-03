package com.darpa.comms;

interface CommServiceReporter {
	void reportReceivedMessage(in byte[] inMessageBytes);
}