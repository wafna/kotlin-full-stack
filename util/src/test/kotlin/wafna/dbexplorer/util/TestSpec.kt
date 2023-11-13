package wafna.dbexplorer.util

import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

class TestSpec {

    internal class SimpleTest {
        @BeforeClass
        fun setUp() {
            // code that will be invoked when this test is instantiated
        }

        @Test(groups = ["fast"])
        fun aFastTest() {
            println("Fast test")
        }

        @Test(groups = ["slow"])
        fun aSlowTest() {
            println("Slow test")
        }
    }
}
