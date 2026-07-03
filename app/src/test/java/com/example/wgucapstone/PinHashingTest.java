package com.example.wgucapstone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.example.wgucapstone.security.PinHasher;

import org.junit.Test;

/**
 * Test plan (see capstone Task 3 Section D write-up):
 *
 * Test                                    | Expected                                        | Pass?
 * -----------------------------------------|--------------------------------------------------|------
 * Hash same PIN + same salt twice           | Identical hash both times                        | see below
 * Hash same PIN with two different salts    | Different hashes                                 | see below
 * Hash two different PINs with same salt    | Different hashes                                 | see below
 * Generated salts are unique                | Two generateSalt() calls don't collide           | see below
 */
public class PinHashingTest {

    @Test
    public void sameInputSameSalt_producesIdenticalHash() {
        String salt = PinHasher.generateSalt();
        assertEquals(PinHasher.hash("1234", salt), PinHasher.hash("1234", salt));
    }

    @Test
    public void samePin_differentSalt_producesDifferentHash() {
        String saltA = PinHasher.generateSalt();
        String saltB = PinHasher.generateSalt();
        assertNotEquals(PinHasher.hash("1234", saltA), PinHasher.hash("1234", saltB));
    }

    @Test
    public void differentPin_sameSalt_producesDifferentHash() {
        String salt = PinHasher.generateSalt();
        assertNotEquals(PinHasher.hash("1234", salt), PinHasher.hash("4321", salt));
    }

    @Test
    public void generatedSalts_areUnique() {
        assertNotEquals(PinHasher.generateSalt(), PinHasher.generateSalt());
    }
}
