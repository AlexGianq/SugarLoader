package org.lambdaloader.alex.lambdaloader;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Alexandre Gianquinto
 */

public class Application extends android.app.Application {

    /**
     * Stupid atomic counter, attached to application
     */
    public final AtomicInteger counter = new AtomicInteger(0);

}
