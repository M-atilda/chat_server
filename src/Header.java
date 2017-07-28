// file:    Header.java
// author:  uma
// date:    17-07-28
package src;


class Header
{
    int m_id;
    int m_kind;
    byte[] m_passphrase;
    public Header(int _id, int _kind, byte[] _pass)
    {
        m_id = _id;
        m_kind = _kind;
        m_passphrase = _pass;
    }
}

