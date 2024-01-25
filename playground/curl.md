# Curl example

[OCR API Curl](https://ocr.space/OCRAPI#curl)  

```bash
curl --location 'https://api.ocr.space/parse/image' \
--header 'apikey: helloworld' \
--form 'language="eng"' \
--form 'isOverlayRequired="false"' \
--form 'isTable="true"' \
--form "base64Image=\"data:image/jpeg;base64,$(base64 -w0 data/costco-bw.jpg)\""
```

