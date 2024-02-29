
/**
  mkdir bin
  javac -d bin OCRClient.java
  java -cp bin OCRClient --table ../../data/receipt-en.jpg | jq '.ParsedResults[0].ParsedText'
*/
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;

class OCRClient {
  public static void main(String[] args) throws Exception {
    String baseUrl = "https://api.ocr.space/";
    String apiKey = "helloworld";
    String language = "eng";
    boolean isTable = false;
    String mimeType = "image/jpeg";
    String imageFilePath = null;
    int i;
    for (i = 0; i < args.length; ++i) {
      if ("--server".equals(args[i])) {
        if (++i < args.length) {
          baseUrl = args[i];
        }
      } else if ("--apikey".equals(args[i])) {
        if (++i < args.length) {
          apiKey = args[i];
        }
      } else if ("--language".equals(args[i])) {
        if (++i < args.length) {
          language = args[i];
        }
      } else if ("--table".equals(args[i])) {
        isTable = true;
      } else if ("--mime".equals(args[i])) {
        if (++i < args.length) {
          mimeType = args[i];
        }
      } else if (imageFilePath == null) {
        imageFilePath = args[i];
      } else {
        break;
      }
    }
    if (i != args.length || imageFilePath == null) {
      System.out.println(
          "Format: java OCRClient [--server <server>] [--apikey <apikey>] [--language <language>] [--table] [--mime <mime>] path");
      System.exit(-1);
    }

    System.out.println(
        new OCRClient(new URL(baseUrl), apiKey)
            .parseImage(language, isTable, mimeType, new FileInputStream(imageFilePath)));
  }

  private final URL baseUrl;
  private final String apiKey;

  public OCRClient(URL baseUrl, String apiKey) {
    this.baseUrl = baseUrl;
    this.apiKey = apiKey;
  }

  public String parseImage(String language, boolean isTable, String mimeType, InputStream image)
      throws MalformedURLException, IOException {
    var url = new URL(this.baseUrl, "parse/image");
    var conn = (HttpURLConnection) url.openConnection();

    try {
      conn.setUseCaches(false);
      conn.setDoOutput(true);
      conn.setDoInput(true);

      //
      // write request
      //

      conn.setRequestProperty("apikey", this.apiKey);

      var formData = new FormData();
      conn.setRequestProperty("Content-Type", formData.mimeType);

      try (var writer = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8), true)) {
        formData.writeField(writer, "language", language);
        if (isTable) {
          formData.writeField(writer, "isTable", "true");
        }
        formData.writeDataField(writer, "base64Image", mimeType, image);
        formData.writeEnd(writer);
      }

      //
      // read response
      //

      try (var ins = conn.getInputStream()) {
        return new String(ins.readAllBytes(), StandardCharsets.UTF_8);
      }

    } finally {
      conn.disconnect();
    }
  }

  static class FormData {
    public final String mimeType;
    private final String boundary;

    public FormData() {
      var bytes = new byte[12];
      new Random().nextBytes(bytes);
      this.boundary = String.format("------------%s", Base64.getUrlEncoder().withoutPadding().encodeToString(bytes));
      this.mimeType = String.format("multipart/form-data;boundary=\"%s\"", boundary);
    }

    public void writeEnd(Writer writer) throws IOException {
      writer.append(String.format("--%s--\r\n", boundary))
          .flush();
    }

    public void writeDataField(Writer writer, String name, String mimeType, InputStream ins) throws IOException {
      var sb = new StringBuilder()
          .append(String.format("data:%s;base64,", mimeType))
          .append(Base64.getEncoder().encodeToString(ins.readAllBytes()));
      writeField(writer, "base64Image", sb.toString());
    }

    private void writeField(Writer writer, String name, String value) throws IOException {
      writer.append(String.format("--%s\r\n", boundary))
          .append(String.format("Content-Disposition: form-data; name=\"%s\"\r\n\r\n", name))
          .append(value).append("\r\n")
          .flush();
    }
  }
}
