# OCR client for receipt extraction

[OCR API](https://ocr.space/OCRAPI)  

Using Spring Boot framework implement an `ecrexporter` tool that given a an image/PDF file, extracts the text from it using OCR (Optical Character Recognition) using the API provided by https://ocr.space/ and saves it to a target location: pdf, text, word or db

The tool supports the following options:

`--url=location` to the image/PDF to be OCRed;  
`--format=pdf|text|word|db`;  
`--location=location` of the file to be saved or JDBC URL in case a db format is specified;

### Curl example

[OCR API Curl](https://ocr.space/OCRAPI#curl)  

```bash
curl --location 'https://api.ocr.space/parse/image' \
--header 'apikey: helloworld' \
--form 'language="eng"' \
--form 'isOverlayRequired="false"' \
--form 'isTable="true"' \
--form "base64Image=\"data:image/jpeg;base64,$(base64 -w0 data/costco-bw.jpg)\""
```
