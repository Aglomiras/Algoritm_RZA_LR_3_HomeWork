package org.example.iec61850.lodicalNodes.protection;

import lombok.Getter;
import lombok.Setter;
import org.example.iec61850.lodicalNodes.LN;
import org.example.iec61850.node_parameters.DataObject.controls.INC;
import org.example.iec61850.node_parameters.DataObject.measured_and_metered_values.WYE;
import org.example.iec61850.node_parameters.DataObject.settings.ASG;
import org.example.iec61850.node_parameters.DataObject.settings.ING;
import org.example.iec61850.node_parameters.DataObject.settings.SPG;
import org.example.iec61850.node_parameters.DataObject.status_information.ACD;
import org.example.iec61850.node_parameters.DataObject.status_information.ACT;
import org.example.iec61850.node_parameters.DataObject.status_information.SPS;

@Getter
@Setter
public class RPSB extends LN {
    /**
     * LN: Power swing detection/blocking Name: RPSB  (LN: Название системы обнаружения/блокировки перепадов мощности: RPSB)
     * Описание этого LN приведено в стандарте IEC 61850-5. Колебание мощности характеризуется медленным периодическим
     * изменением измеряемого сопротивления. Такое умеренное изменение сопротивления допустимо, но это может привести к
     * отключению функции дистанционной защиты. Если генератор выходит из строя (проскальзывание полюсов),
     * измеряются кратковременные изменения полного сопротивления (по одному на каждое проскальзывание).
     * После небольшого количества сбоев (MaxNumSlp) в течение определенного временного интервала (EvTmms)
     * генератор должен быть отключен во избежание механических повреждений (ступенчатое отключение).
     * Фактическое количество квитанций должно быть обнулено либо во время поездки, либо по истечении времени оценки.
     * */
    /**
     * Status information
     */
    private ACD Str = new ACD(); //
    public ACT Op = new ACT(); //
    private SPS BlkZn = new SPS();
    /**
     * Controls
     */
    private INC OpCntRs = new INC();
    /**
     * Settings
     */
    private SPG ZeroEna = new SPG();
    private SPG NgEna = new SPG();
    private SPG MaxEna = new SPG();
    private ASG SwgVal = new ASG(); //
    private ASG SwgRis = new ASG();
    private ASG SwgReact = new ASG();
    private ING SwgTmms = new ING();
    private ING UnBlkTmms = new ING();
    private ING MaxNumSlp = new ING();
    private ING EvTmms = new ING();

    /**
     * Input
     */
    public WYE Z = new WYE();
    public double[] zListA = new double[]{0.0, 0.0};
    public double[] zListB = new double[]{0.0, 0.0};
    public double[] zListC = new double[]{0.0, 0.0};
    public ASG modZ = new ASG(); //Уставка по шагу сопротивления
    public int count = 0;
    public boolean flag = false;

    public RPSB() {
        this.Op.getGeneral().setValue(false);
    }

    @Override
    public void process() {
        /**Сдвиг значений*/
        zListA[1] = zListA[0];
        zListA[0] = Z.getPhsA().getCVal().getMag().getFloatVal().getValue();

        zListB[1] = zListB[0];
        zListB[0] = Z.getPhsB().getCVal().getMag().getFloatVal().getValue();

        zListC[1] = zListC[0];
        zListC[0] = Z.getPhsC().getCVal().getMag().getFloatVal().getValue();

        if (count != 0) {
            Str.getPhsA().setValue(check(zListA[0], zListA[1]));
            Str.getPhsB().setValue(check(zListB[0], zListB[1]));
            Str.getPhsC().setValue(check(zListC[0], zListC[1]));
            Str.getGeneral().setValue(Str.getPhsA().getValue() || Str.getPhsB().getValue() || Str.getPhsC().getValue());
            Op.getGeneral().setValue(Str.getGeneral().getValue());

//            System.out.println(Op.getGeneral().getValue());
        }
        this.count++;
    }

    public boolean check(double a, double b) {
        return !(Math.abs(a - b) < modZ.getSetMag().getFloatVal().getValue());
    }
}
