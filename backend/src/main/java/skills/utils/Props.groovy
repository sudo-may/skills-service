package skills.utils

import groovy.transform.CompileStatic
import org.springframework.beans.BeanUtils

@CompileStatic
class Props {
    static Object copy(Object source, Object target)  {
        // ignore groovy artifacts
        BeanUtils.copyProperties(source, target, "class", "metaClass")
        return target
    }

    static Object copy(Object source, Object target, String... ignoreProperties)  {
        List<String> ignore = ["class", "metaClass"]
        ignore.addAll(ignoreProperties)

        String[] ignoreProps = ignore.toArray(new String[0])
        // ignore groovy artifacts
        BeanUtils.copyProperties(source, target, ignoreProps)

        return target
    }
}
