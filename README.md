# OCR client for receipt extraction

[OCR API](https://ocr.space/OCRAPI)  

Using Spring Boot framework implement an `ecrexporter` tool that given a an image/PDF file, extracts the text from it using OCR (Optical Character Recognition) using the API provided by https://ocr.space/ and saves it to a target location: pdf, text, word or db

The tool supports the following options:

`--url=location` to the image/PDF to be OCRed;  
`--format=pdf|text|word|db`;  
`--location=location` of the file to be saved or JDBC URL in case a db format is specified;
