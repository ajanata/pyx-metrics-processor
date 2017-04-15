/**
 * Copyright (c) 2017, Andy Janata
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this list of conditions
 *   and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice, this list of
 *   conditions and the following disclaimer in the documentation and/or other materials provided
 *   with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.socialgamer.pyx.metrics.processor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Properties;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.socialgamer.pyx.metrics.inject.ProcessorModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;


public class Processor {

  private static Properties loadProperties(final File file) throws IOException {
    final Properties props = new Properties();
    props.load(new FileInputStream(file));
    return props;
  }

  private static void showUsageAndExit(final OptionParser optParser, final PrintStream sink,
      final int exitCode) throws IOException {
    sink.println(String.format("USAGE: %s [options]", Processor.class.getSimpleName()));
    sink.println();
    optParser.printHelpOn(sink);
    System.exit(exitCode);
  }

  public static void main(final String[] args) throws IOException, InterruptedException {
    // Process command-line options
    final OptionParser optParser = new OptionParser(false);
    final OptionSpec<Void> help = optParser.acceptsAll(Arrays.asList("h", "help"),
        "Print this usage information.");
    final OptionSpec<Void> reset = optParser.acceptsAll(Arrays.asList("r", "reset"),
        "Reset offset information and quit. IMPORTANT: There MUST NOT be any instances of this "
            + "consumer group running on any machine when using this option.");
    final OptionSpec<File> conf = optParser
        .acceptsAll(Arrays.asList("c", "configuration"), "Configuration file to use.")
        .withRequiredArg()
        .describedAs("file")
        .ofType(File.class)
        .defaultsTo(new File("processor.properties"));
    final OptionSet opts = optParser.parse(args);
    if (opts.has(help)) {
      showUsageAndExit(optParser, System.out, 0);
    }
    final File propsFile = opts.valueOf(conf);
    if (!propsFile.canRead()) {
      System.err.println(String.format("Unable to open configuration file %s for reading.",
          propsFile.getAbsolutePath()));
      System.err.println();
      showUsageAndExit(optParser, System.err, 1);
    }

    // Load configuration
    final Properties appProps;
    try {
      appProps = loadProperties(propsFile);
    } catch (final IOException e) {
      throw new RuntimeException("Unable to load properties", e);
    }

    // Create injector
    final Injector injector = Guice.createInjector(Stage.PRODUCTION, new ProcessorModule(appProps));
    final Consumer cons = injector.getInstance(Consumer.class);

    if (opts.has(reset)) {
      System.out.println("Resetting consumer offsets.");
      cons.resetOffsets();
      System.out.println("Consumer offsets reset.");
      System.exit(0);
    }

    // FIXME temporary for debugging
    //    cons.resetOffsets();

    final Thread t = new Thread(cons);
    t.start();

    // FIXME temporary for speed
    //    for (int i = 0; i < 4; i++) {
    //      final Consumer anotherCons = injector.getInstance(Consumer.class);
    //      new Thread(anotherCons).start();
    //    }

    t.join();
  }
}
