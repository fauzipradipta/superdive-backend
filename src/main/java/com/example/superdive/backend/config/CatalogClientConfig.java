package com.example.superdive.backend.config;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.superdive.catalog.v1.CatalogServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * Client wiring for the superdive-catalog gRPC service.
 *
 * The backend is a gRPC client only. It speaks to catalog over this channel
 * instead of reaching into the `product` table, so the catalog service stays
 * the single writer of its own data.
 */
@Configuration
public class CatalogClientConfig {

	/**
	 * One long-lived channel for the whole application. A gRPC channel multiplexes
	 * concurrent calls over HTTP/2 and manages its own connections, so creating
	 * one per call would throw away connection reuse for no benefit.
	 */
	@Bean(destroyMethod = "shutdownNow")
	public ManagedChannel catalogChannel(
			@Value("${superdive.catalog.host:localhost}") String host,
			@Value("${superdive.catalog.port:9090}") int port) {

		return ManagedChannelBuilder.forAddress(host, port)
				// Plaintext: catalog is an internal service on a private network.
				// This needs TLS before the two ever talk across a public hop.
				.usePlaintext()
				.build();
	}

	@Bean
	public CatalogServiceGrpc.CatalogServiceBlockingStub catalogStub(
			ManagedChannel catalogChannel,
			@Value("${superdive.catalog.deadline-seconds:5}") long deadlineSeconds) {

		// A deadline on every call. Without one a stalled catalog would hold
		// backend request threads open until they time out at the HTTP layer,
		// turning one slow dependency into a backend-wide outage.
		return CatalogServiceGrpc.newBlockingStub(catalogChannel)
				.withDeadlineAfter(deadlineSeconds, TimeUnit.SECONDS);
	}
}
