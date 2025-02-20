package com.kustacks.kuring.archunit;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@DisplayName("헥사고갈 아키텍처 검증")
class DependencyRuleTests {

	@DisplayName("User 아키텍처 검증")
	@Test
	void validateUserArchitecture() {
		HexagonalArchitecture.boundedContext("com.kustacks.kuring.user")

				.withDomainLayer("domain")

				.withAdaptersLayer("adapter")
				.incoming("in.web")
				.outgoing("out.persistence")
				.outgoing("out.event")
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

	@DisplayName("Notice 아키텍처 검증")
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

	@DisplayName("Admin 아키텍처 검증")
	@Test
	void validateAdminArchitecture() {
		HexagonalArchitecture.boundedContext("com.kustacks.kuring.admin")

				.withDomainLayer("domain")

				.withAdaptersLayer("adapter")
				.incoming("in.web")
				.outgoing("out.persistence")
				.outgoing("out.event")
				.and()

				.withApplicationLayer("application")
				.services("service")
				.incomingPorts("port.in")
				.outgoingPorts("port.out")
				.and()

				.withConfiguration("configuration")
				.check(new ClassFileImporter()
						.importPackages("com.kustacks.kuring.admin.."));
	}

	@DisplayName("Staff 아키텍처 검증")
	@Test
	void validateStaffArchitecture() {
		HexagonalArchitecture.boundedContext("com.kustacks.kuring.staff")

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
						.importPackages("com.kustacks.kuring.staff.."));
	}

	@DisplayName("Email 아키텍처 검증")
	@Test
	void validateEmailArchitecture() {
		HexagonalArchitecture.boundedContext("com.kustacks.kuring.email")

				.withDomainLayer("domain")

				.withAdaptersLayer("adapter")
				.incoming("in.web")
				.outgoing("out.persistence")
				.outgoing("out.email")
				.and()

				.withApplicationLayer("application")
				.services("service")
				.incomingPorts("port.in")
				.outgoingPorts("port.out")
				.and()

				.withConfiguration("configuration")
				.check(new ClassFileImporter()
						.importPackages("com.kustacks.kuring.email.."));
	}

	@DisplayName("테스트 페키지 의존성 검증")
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
