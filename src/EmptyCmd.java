// file:    EmptyCmd.java
// author:  uma
// date:    17-07-28
package src;

public class EmptyCmd extends AbstractCommand
{
    @Override
    String getName() { return "EmptyCmd"; }

    @Override
    protected void make_header() {}

    @Override
    protected void make_contents() {}
} // EmptyCmd

