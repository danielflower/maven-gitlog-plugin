package com.github.danielflower.mavenplugins.gitlog.renderers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Test;

public class RegexFilterMessageConverterTest {


    @Test
    public void testEmpty(){
        RegexFilterMessageConverter converter=new RegexFilterMessageConverter("((SOF|SAP)-\\d+)", null);
        assertThat(converter.formatCommitMessage("lkjlkj"),equalTo(""));
    }

    @Test
    public void testReplace1(){
        RegexFilterMessageConverter converter=new RegexFilterMessageConverter("((SOF|SAP)-\\d+)", null);
        assertThat(converter.formatCommitMessage("hej hopp SOF-42 ugga bugga "),equalTo("SOF-42"));
    }

    @Test
    public void testReplace12(){
        RegexFilterMessageConverter converter=new RegexFilterMessageConverter("((SOF|SAP)-\\d+)", null);
        assertThat(converter.formatCommitMessage("SAP-42"),equalTo("SAP-42"));
    }


    @Test
    public void testReplace2(){
        RegexFilterMessageConverter converter=new RegexFilterMessageConverter("((SOF|SAP)-\\d+)&&&(hejamora)", null);
        assertThat(converter.formatCommitMessage("heja SAP-42 mora hejamora"),equalTo("SAP-42 hejamora"));
    }

    @Test
    public void testReplaceWithConverter(){
        JiraIssueLinkConverter c = new JiraIssueLinkConverter(new SystemStreamLog(),
            "https://jira.atlassian.com/browse/CONF/");
        RegexFilterMessageConverter converter=new RegexFilterMessageConverter("((SOF|SAP)-\\d+)&&&(hejamora)", c);
        assertThat(converter.formatCommitMessage("heja SAP-42 mora hejamora"),equalTo("<a href=\"https://jira.atlassian.com/browse/SAP-42\">SAP-42</a> hejamora"));
    }


}
