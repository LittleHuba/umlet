package com.baselet.api;

import com.baselet.control.config.handler.ConfigHandler;
import com.baselet.control.enums.Program;
import com.baselet.control.enums.RuntimeType;
import com.baselet.control.util.Utils;
import com.baselet.diagram.DiagramHandler;
import com.baselet.diagram.io.OutputHandler;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static com.baselet.control.util.Utils.readBuildInfo;


public class Converter {

  public static void main(String[] args) throws Exception {
    InputStream analysis = Thread.currentThread().getContextClassLoader().getResourceAsStream("./cl_analysis.uxf");
    Files.write(Paths.get("analysis.png"), convert(read(analysis), "png"));

    InputStream object = Thread.currentThread().getContextClassLoader().getResourceAsStream("./cl_object.uxf");
    Files.write(Paths.get("object.png"), convert(read(object), "png"));

    InputStream struct = Thread.currentThread().getContextClassLoader().getResourceAsStream("./kxd_composite_structure.uxf");
    Files.write(Paths.get("struct.png"), convert(read(struct), "png"));

    InputStream state = Thread.currentThread().getContextClassLoader().getResourceAsStream("./sm_complex_state.uxf");
    Files.write(Paths.get("state.png"), convert(read(state), "png"));
  }

  public static String read(InputStream input) throws IOException {
    try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
      return buffer.lines().collect(Collectors.joining("\n"));
    }
  }

  public static byte[] convert(String source, String format) throws Exception {
    Utils.BuildInfo buildInfo = readBuildInfo();
    Program.init(buildInfo.version, RuntimeType.BATCH);
    ConfigHandler.loadConfig();
    DiagramHandler handler = DiagramHandler.forExport(source);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputHandler.createToStream(format, baos, handler);
    return baos.toByteArray();
  }
}
