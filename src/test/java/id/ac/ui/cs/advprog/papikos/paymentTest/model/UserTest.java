package id.ac.ui.cs.advprog.papikos.paymentTest.model;

import id.ac.ui.cs.advprog.papikos.auth.entity.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    void testBalanceModification() {
        User user = new User();
        user.setBalance(100.0);

        user.increaseBalance(50.0);
        assertEquals(150.0, user.getBalance());

        user.decreaseBalance(25.0);
        assertEquals(125.0, user.getBalance());
    }
}
