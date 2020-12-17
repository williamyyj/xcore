package org.cc.ende;

import org.junit.Assert;
import org.junit.Test;

public class LZFTest {
		@Test
		public void test_encode_decode(){
				String data = "Will是好人______________________________________________________1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111________________________________________________________";
				String encode = LZFEnde.encoder(data);
				System.out.println(encode);
				String decode = LZFEnde.decoder(encode);
			System.out.println(decode);
			Assert.assertEquals(data,decode);
		}
}
