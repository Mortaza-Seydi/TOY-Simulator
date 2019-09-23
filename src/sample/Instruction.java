package sample;

public class Instruction
{
    private String label;
    private String opCode;
    private String address;

    private Boolean flag;

    public Instruction(String label, String opCode, String address, Boolean flag)
    {
        this.label = label;
        this.opCode = opCode;
        this.address = address;
        this.flag = flag;
    }

    public String getLabel()
    {
        return label;
    }

    public String getOpCode()
    {
        return opCode;
    }

    public String getAddress()
    {
        return address;
    }

    public Boolean isFlag()
    {
        return flag;
    }

    public void setFlag(Boolean flag)
    {
        this.flag = flag;
    }
}