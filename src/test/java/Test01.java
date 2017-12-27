import java.util.Arrays;
import java.util.List;

import com.nemustech.common.util.Utils;

import lombok.AllArgsConstructor;
import lombok.Data;

public class Test01 {
	public void test01() {
		Test02[] array = new Test02[] { new Test02(1, "a"), new Test02(2, "b"), new Test02(3, "b") };

		Object obj = Arrays.stream(array).filter(test02 -> "b".equals(test02.getName())).iterator();
		System.out.println(obj.getClass().getName() + ": " + Utils.toString(obj));

		obj = Arrays.stream(array).filter(test02 -> "b".equals(test02.getName())).toArray();
		System.out.println(obj.getClass().getName() + ": " + Utils.toString(obj));

		List<Test02> list = Arrays.asList(array);
		obj = list.parallelStream().filter(test02 -> "b".equals(test02.getName())).findFirst().get();
		System.out.println(obj.getClass().getName() + ": " + obj);
	}

	@Data
	@AllArgsConstructor
	class Test02 {
		long id;
		String name;
	}

	public static void main(String[] args) {
		new Test01().test01();
	}
}