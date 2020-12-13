package io.github.nandandesai.secure.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class PDF {
    private byte[] pdfBytes;
    private String mimeType;
}
