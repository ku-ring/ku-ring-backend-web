package com.kustacks.kuring.archunit;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class DependencyRuleTests {

	@DisplayName("User 헥사고갈 아키텍처 검증")
	@Test
	void validateUserArchitecture() {
		HexagonalArchitecture.boundedContext("com.kustacks.kuring.user")

				.withDomainLayer("domain")

				.withAdaptersLayer("adapter")
				.incoming("in.web")
				.outgoing("out.persistence")
				.and()

				.withApplicationLayer("application")
				.services("service")
				.incomingPorts("port.in")
				.outgoingPorts("port.out")
				.and()

				.withConfiguration("configuration")
				.check(new ClassFileImporter()
						.importPackages("com.kustacks.kuring.user.."));
	}

	@DisplayName("Notice 헥사고갈 아키텍처 검증")
	@Test
	void validateNoticeArchitecture() {
		HexagonalArchitecture.boundedContext("com.kustacks.kuring.notice")

				.withDomainLayer("domain")

				.withAdaptersLayer("adapter")
				.incoming("in.web")
				.outgoing("out.persistence")
				.and()

				.withApplicationLayer("application")
				.services("service")
				.incomingPorts("port.in")
				.outgoingPorts("port.out")
				.and()

				.withConfiguration("configuration")
				.check(new ClassFileImporter()
						.importPackages("com.kustacks.kuring.notice.."));
	}

	@Test
	void testPackageDependencies() {
		noClasses()
				.that()
				.resideInAPackage("com.kustacks.kuring.user.domain..")
				.should()
				.dependOnClassesThat()
				.resideInAnyPackage("com.kustacks.kuring.user.application..")
				.check(new ClassFileImporter()
						.importPackages("com.kustacks.kuring.user.."));
	}

}
