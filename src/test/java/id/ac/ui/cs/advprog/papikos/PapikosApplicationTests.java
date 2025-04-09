package id.ac.ui.cs.advprog.papikos;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PapikosApplicationTests {

	@Test
	void contextLoads() {
		// This already tests if Spring context loads successfully
	}

	@Test
	void mainMethodRuns() {
		PapikosApplication.main(new String[] {});
	}
}
