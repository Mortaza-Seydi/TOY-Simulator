package sample;

public class Memory
{
    private String name;
    private String value;
    private String address;

    public Memory(String name, String value, String address)
    {
        this.name = name;
        this.value = value;
        this.address = address;
    }

    public String getName()
    {
        return name;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getAddress()
    {
        return address;
    }
}
