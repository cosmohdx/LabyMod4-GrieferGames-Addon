package de.cosmohdx.griefergames.payload;

import java.io.DataInput;
import java.io.IOException;

/**
 * Decodes the body of one {@code griefergames:main} payload. The id has already been read.
 * Extra bytes after the known fields are ignored so the server can append fields later.
 */
public final class PayloadCodec<T extends ClientPayload> {

  @FunctionalInterface
  public interface Decoder<T> {
    T decode(DataInput input) throws IOException;
  }

  private final String id;
  private final Decoder<T> decoder;

  private PayloadCodec(String id, Decoder<T> decoder) {
    this.id = id;
    this.decoder = decoder;
  }

  public static <T extends ClientPayload> PayloadCodec<T> of(String id, Decoder<T> decoder) {
    return new PayloadCodec<>(id, decoder);
  }

  public String id() {
    return id;
  }

  public T decode(DataInput input) throws IOException {
    return decoder.decode(input);
  }
}
