package org.taymyr.lagom.elasticsearch.indices.dsl;

import org.jetbrains.annotations.NotNull;

class AutocompleteFilter extends Filter {

    private Integer minGram;
    private Integer maxGram;

    public AutocompleteFilter(@NotNull String type, Integer minGram, Integer maxGram) {
        super(type);
        this.minGram = minGram;
        this.maxGram = maxGram;
    }

    public Integer getMinGram() {
        return minGram;
    }

    public void setMinGram(Integer minGram) {
        this.minGram = minGram;
    }

    public Integer getMaxGram() {
        return maxGram;
    }

    public void setMaxGram(Integer maxGram) {
        this.maxGram = maxGram;
    }
}