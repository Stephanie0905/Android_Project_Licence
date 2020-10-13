package com.example.kelys.Models;

import java.util.List;

public class ModelOption {
    private List<String>  options;

    public ModelOption() {

    }

    public ModelOption(List<String> options) {
        this.options = options;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    /*
    public List<String> getOptions() {
        return  this.options;
    }

    public void setOptions(List<String> newOptions) {
        this.options = newOptions;
    }

    */


}
