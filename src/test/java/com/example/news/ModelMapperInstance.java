package com.example.news;

import org.modelmapper.convention.MatchingStrategies;

public class ModelMapperInstance {

    private ModelMapperInstance() {

        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public org.modelmapper.ModelMapper modelMapper = new org.modelmapper.ModelMapper();

    private static class InnerInstanceClazz {
        private static final ModelMapperInstance instance = new ModelMapperInstance();
    }

    public static ModelMapperInstance getInstance() {
        return InnerInstanceClazz.instance;
    }
}
