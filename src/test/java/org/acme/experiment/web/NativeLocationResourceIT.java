package org.acme.experiment.web;

import io.quarkus.test.junit.NativeImageTest;
import org.acme.experiment.web.LocationResourceTest;

@NativeImageTest
public class NativeLocationResourceIT extends LocationResourceTest {

    // Execute the same tests but in native mode.
}