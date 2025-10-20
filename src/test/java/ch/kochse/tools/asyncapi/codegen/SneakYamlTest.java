package ch.kochse.tools.asyncapi.codegen;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

class SneakYamlTest {
	private static Yaml yamlParser = null; 
	private List<String> keySeq = null; 
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		yamlParser = new Yaml();
	}

	@Test
	void test() {
		try {
			keySeq = new ArrayList<>();
			InputStream is = new FileInputStream("./src/test/resources/api/asyncapi.yaml");
			Map<String, Object> map = yamlParser.load(is);
			dumpMap(map);
		} catch (FileNotFoundException pEx) {
			// TODO Auto-generated catch block
			pEx.printStackTrace();
		}
	}
	
	private void dumpMap(Map<String, Object> pMap) {
		if (pMap != null) {
				String prefix = genKey();
				for (String key : pMap.keySet()) {
				Object nxt = pMap.get(key);
				String cln = nxt.getClass().getSimpleName();
				if (cln.startsWith("LinkedHashMap"))	{
					if (prefix.isEmpty()) {
						System.out.printf("%s::%s\n", cln, key);
					} else {
						System.out.printf("%s::%s.%s\n", cln, prefix, key);
					}
					keySeq.add(key);
					dumpMap((Map<String, Object>) nxt);
					keySeq.remove(keySeq.size()-1);
				} else {
					if (prefix.isEmpty()) {
						System.out.printf("%s::%s = %s\n", cln, key, nxt.toString());
					} else {
						System.out.printf("%s::%s.%s = %s\n", cln, prefix, key, nxt.toString());
					}
					
				}
			}
		}
	}
	
	private String genKey() {
		StringBuilder key = new StringBuilder();
		for (String k : keySeq) {
			if (key.isEmpty()) {
				key.append(k);
			} else {
				key.append('.').append(k);
			}
		}
		return key.toString();
	}

}
