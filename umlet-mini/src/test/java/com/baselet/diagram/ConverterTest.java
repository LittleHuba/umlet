package com.baselet.diagram;

import com.baselet.control.config.handler.ConfigHandler;
import com.baselet.control.enums.Program;
import com.baselet.control.enums.RuntimeType;
import com.baselet.control.util.Utils;
import com.baselet.diagram.io.OutputHandler;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Collectors;

import static com.baselet.control.util.Utils.readBuildInfo;
import static org.assertj.core.api.Assertions.assertThat;

public class ConverterTest {

	@Before
	public void before() {
		Utils.BuildInfo buildInfo = readBuildInfo();
		Program.init(buildInfo.version, RuntimeType.BATCH);
		ConfigHandler.loadConfig();
	}

	// analysis

	@Test
	public void should_convert_analysis_diagram_to_png() throws Exception {
		should_convert_diagram_to_output("cl_analysis.uxf", "png", "analysis.png", "91857AD273BD18615A32339DF9A50FC0");
	}

	@Test
	public void should_convert_analysis_diagram_to_svg() throws Exception {
		should_convert_diagram_to_output("cl_analysis.uxf", "svg", "analysis.svg", "10BE3BA0254F599B80668DACEB02F948");
	}

	@Test
	public void should_convert_analysis_diagram_to_pdf() throws Exception {
		should_convert_diagram_to_pdf("cl_analysis.uxf", "analysis.pdf", 2377);
	}

	@Test
	public void should_convert_analysis_diagram_to_jpeg() throws Exception {
		should_convert_diagram_to_output("cl_analysis.uxf", "jpeg", "analysis.jpeg", "95C6AF195D1BB7F198457830A89CF5E5");
	}

	// object

	@Test
	public void should_convert_object_diagram_to_png() throws Exception {
		should_convert_diagram_to_output("cl_object.uxf", "png", "object.png", "258E7280E7AACBB62C8056447041BFCB");
	}

	@Test
	public void should_convert_object_diagram_to_svg() throws Exception {
		should_convert_diagram_to_output("cl_object.uxf", "svg", "object.svg", "96A0B9B6A7232AF686A17DC63B31C48E");
	}

	@Test
	public void should_convert_object_diagram_to_pdf() throws Exception {
		should_convert_diagram_to_pdf("cl_object.uxf", "object.pdf", 2939);
	}

	@Test
	public void should_convert_object_diagram_to_jpeg() throws Exception {
		should_convert_diagram_to_output("cl_object.uxf", "jpeg", "object.jpeg", "4C4613A149B2909296B5C2DCB1B0E2D6");
	}

	// struct

	@Test
	public void should_convert_struct_diagram_to_png() throws Exception {
		should_convert_diagram_to_output("kxd_composite_structure.uxf", "png", "struct.png", "E7FC176DB4D70C33BD6FC327EE57397E");
	}

	@Test
	public void should_convert_struct_diagram_to_svg() throws Exception {
		should_convert_diagram_to_output("kxd_composite_structure.uxf", "svg", "struct.svg", "46AF25C1382CF0E33D1C89BF0D16EF91");
	}

	@Test
	public void should_convert_struct_diagram_to_pdf() throws Exception {
		should_convert_diagram_to_pdf("kxd_composite_structure.uxf", "struct.pdf", 2547);
	}

	@Test
	public void should_convert_struct_diagram_to_jpeg() throws Exception {
		should_convert_diagram_to_output("kxd_composite_structure.uxf", "jpeg", "struct.jpeg", "EF71C37F178FD394C87E8245207D6631");
	}

	// state

	@Test
	public void should_convert_state_diagram_to_png() throws Exception {
		should_convert_diagram_to_output("sm_complex_state.uxf", "png", "state.png", "C6E72CD2E459073F091529752F9FEF9E");
	}

	@Test
	public void should_convert_state_diagram_to_svg() throws Exception {
		should_convert_diagram_to_output("sm_complex_state.uxf", "svg", "state.svg", "9D35554E37E6C23EB275C0DBDEFA87F3");
	}

	@Test
	public void should_convert_state_diagram_to_pdf() throws Exception {
		should_convert_diagram_to_pdf("sm_complex_state.uxf", "state.pdf", 4081);
	}

	@Test
	public void should_convert_state_diagram_to_jpeg() throws Exception {
		should_convert_diagram_to_output("sm_complex_state.uxf", "jpeg", "state.jpeg", "8F05996D07D6BE005D7DF54F2FB0DAC6");
	}

	// seq

	@Test
	public void should_convert_seq_diagram_to_png() throws Exception {
		should_convert_diagram_to_output("sd_sequence.uxf", "png", "seq.png", "D181D8129FE536AB031E53C8064A8924");
	}

	@Test
	public void should_convert_seq_diagram_to_svg() throws Exception {
		should_convert_diagram_to_output("sd_sequence.uxf", "svg", "seq.svg", "514EE199EDB6D9BD4367D9F47935B99F");
	}

	@Test
	public void should_convert_seq_diagram_to_pdf() throws Exception {
		should_convert_diagram_to_pdf("sd_sequence.uxf", "seq.pdf", 6753);
	}

	@Test
	public void should_convert_seq_diagram_to_jpeg() throws Exception {
		should_convert_diagram_to_output("sd_sequence.uxf", "jpeg", "seq.jpeg", "1078D946955843631C397A4896B07808");
	}

	private void should_convert_diagram_to_output(String diagramName, String outputFormat, String outputPath, String expectedHash) throws IOException, NoSuchAlgorithmException {
		InputStream diagram = Thread.currentThread().getContextClassLoader().getResourceAsStream("./" + diagramName);
		byte[] result = convert(read(diagram), outputFormat);
		// write the file in target to debug
		Files.write(Paths.get("target", outputPath), result);
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(result);
		byte[] digest = md.digest();
		String hash = DatatypeConverter.printHexBinary(digest).toUpperCase();
		assertThat(hash).isEqualTo(expectedHash);
	}

	private void should_convert_diagram_to_pdf(String diagramName, String outputPath, int expectedSize) throws IOException, NoSuchAlgorithmException {
		InputStream diagram = Thread.currentThread().getContextClassLoader().getResourceAsStream("./" + diagramName);
		byte[] result = convert(read(diagram), "pdf");
		// write the file in target to debug
		Files.write(Paths.get("target", outputPath), result);
		int approx = 10;
		assertThat(result.length).isBetween(expectedSize - approx, expectedSize + approx);
	}

	private static String read(InputStream input) throws IOException {
		try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
			return buffer.lines().collect(Collectors.joining("\n"));
		}
	}

	private byte[] convert(String source, String format) throws IOException {
		DiagramHandler handler = DiagramHandler.forExport(source);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OutputHandler.createToStream(format, baos, handler);
		return baos.toByteArray();
	}
}
