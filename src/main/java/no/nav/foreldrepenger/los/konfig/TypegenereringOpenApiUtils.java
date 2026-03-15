package no.nav.foreldrepenger.los.konfig;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.jackson.ModelResolver;
import no.nav.openapi.spec.utils.openapi.DiscriminatorModelConverter;
import no.nav.openapi.spec.utils.openapi.EnumVarnamesConverter;
import no.nav.openapi.spec.utils.openapi.JsonSubTypesModelConverter;
import no.nav.openapi.spec.utils.openapi.NoJsonSubTypesAnnotationIntrospector;
import no.nav.openapi.spec.utils.openapi.PrefixStrippingFQNTypeNameResolver;
import no.nav.openapi.spec.utils.openapi.RefToClassLookup;
import no.nav.vedtak.mapper.json.DefaultJsonMapper;

public class TypegenereringOpenApiUtils {

    private TypegenereringOpenApiUtils() {
    }

    public static void settOppForTypegenereringFrontend() {
        ModelResolver.enumsAsRef = true;
        ModelConverters.reset();

        var typeNameResolver = new PrefixStrippingFQNTypeNameResolver("no.nav.foreldrepenger.los.", "no.nav.");
        typeNameResolver.setUseFqn(true);

        ModelConverters.getInstance().addConverter(new ModelResolver(lagObjectMapperUtenJsonSubTypeAnnotasjoner(), typeNameResolver));
        ModelConverters.getInstance().addConverter(new JsonSubTypesModelConverter());
        ModelConverters.getInstance().addConverter(new DiscriminatorModelConverter(new RefToClassLookup()));
        ModelConverters.getInstance().addConverter(new EnumVarnamesConverter());
    }

    private static JsonMapper lagObjectMapperUtenJsonSubTypeAnnotasjoner() {
        return DefaultJsonMapper.getJsonMapper().rebuild()
            .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
            .enable(MapperFeature.SORT_CREATOR_PROPERTIES_FIRST)
            .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
            .annotationIntrospector(new NoJsonSubTypesAnnotationIntrospector())
            .build();
    }
}
