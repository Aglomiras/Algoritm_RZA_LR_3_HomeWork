package org.example.iec61850.lodicalNodes.protection;

import lombok.Getter;
import lombok.Setter;
import org.example.iec61850.common.modelData.Attribute;
import org.example.iec61850.lodicalNodes.LN;
import org.example.iec61850.node_parameters.DataObject.CMV;
import org.example.iec61850.node_parameters.DataObject.controls.INC;
import org.example.iec61850.node_parameters.DataObject.measured_and_metered_values.SEQ;
import org.example.iec61850.node_parameters.DataObject.measured_and_metered_values.WYE;
import org.example.iec61850.node_parameters.DataObject.settings.ASG;
import org.example.iec61850.node_parameters.DataObject.settings.ENG;
import org.example.iec61850.node_parameters.DataObject.settings.ING;
import org.example.iec61850.node_parameters.DataObject.settings.SPG;
import org.example.iec61850.node_parameters.DataObject.status_information.ACD;
import org.example.iec61850.node_parameters.DataObject.status_information.ACT;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class PDIS extends LN {
    /**
     * LN: Distance Name: PDIS (LN: Название дистанции: PDIS)
     *
     Описание этого LN приведено в IEC 61850-5. Начальное значение фазы и начальное значение заземления являются минимальными пороговыми значениями
     для снятия измерений импеданса в зависимости от функциональной характеристики расстояния, заданной алгоритмом и определенной настройками.
     Эти настройки заменяют кривую объекта данных, используемую для характеристики на какой-либо другой защитной линзе.
     Для каждой зоны должен использоваться один экземпляр PDI.
     * */

    /**
     * Status information
     */
    private ACD Str = new ACD(); //Output: на срабатывание защиты
    private ACT Op = new ACT(); //Output: на отключение оборудования
    /**
     * Controls
     */
    private INC OpCntRs = new INC(); //Счетчик операций
    /**
     * Settings
     */
    private ASG PoRch = new ASG();
    private ASG PhStr = new ASG(); //Начальная фаза
    private ASG GndStr = new ASG();
    private ENG DirMod = new ENG(); //Направленность
    private ASG PctRch = new ASG();
    private ASG Ofs = new ASG();
    private ASG PctOfs = new ASG();
    private ASG RisLod = new ASG();
    private ASG AngLod = new ASG();
    private SPG TmDlMod = new SPG();
    private ING OpDlTmms = new ING(); //Уставка по времени
    private SPG PhDlMod = new SPG();
    private ING PhDlTmms = new ING();
    private SPG GndDlMod = new SPG();
    private ING GndDlTmms = new ING();
    private ASG X1 = new ASG();
    private ASG LinAng = new ASG();
    private ASG RisGndRch = new ASG();
    private ASG RisPhRch = new ASG();
    private ASG K0Fact = new ASG();
    private ASG K0FactAng = new ASG();
    private ING RsDlTmms = new ING();

    /**
     * Input
     */
    public SEQ SeqA = new SEQ();
    private WYE Z = new WYE();
    public ACT blkDZ = new ACT();
    public Attribute<Boolean> boost = new Attribute<>(); //Наличие ускорения
    public double modific = 0.25;
    public int counter = 0;
    public boolean flagDZ = false;

    public Map<Integer, double[]> line_coeff = new HashMap<>();

    public PDIS() {
        /**Устанавливаем false: означает, что оборудование еще НЕ отключено*/
        Op.getGeneral().setValue(false); //Управляющее действие

        /**Установка начального набора времени (счетчика операций)*/
        OpCntRs.getStVal().setValue(0);

        /**Ускорение*/
        boost.setValue(false);
        blkDZ.getGeneral().setValue(false);
    }

    @Override
    public void process() {
        double[] decomA = decomposition(this.Z.getPhsA());
        double[] decomB = decomposition(this.Z.getPhsB());
        double[] decomC = decomposition(this.Z.getPhsC());

        Str.getPhsA().setValue(!checkX(decomA) && !checkY(decomA));
        Str.getPhsB().setValue(!checkX(decomB) && !checkY(decomB));
        Str.getPhsC().setValue(!checkX(decomC) && !checkY(decomC));

        Str.getGeneral().setValue(Str.getPhsA().getValue() || Str.getPhsB().getValue() || Str.getPhsC().getValue());
        Op.getGeneral().setValue(Str.getPhsA().getValue() || Str.getPhsB().getValue() || Str.getPhsC().getValue());

        if (Op.getGeneral().getValue()) {
            OpCntRs.getStVal().setValue(OpCntRs.getStVal().getValue() + 1); //Набор времени
        } else {
            OpCntRs.getStVal().setValue(0); //Сброс счетчика времени
        }

        if (Op.getGeneral().getValue() && (OpCntRs.getStVal().getValue() * modific > OpDlTmms.getSetVal().getValue()) && !blkDZ.getGeneral().getValue()) {//
            Op.getPhsA().setValue(true);
            Op.getPhsB().setValue(true);
            Op.getPhsC().setValue(true);
        } else {
            Op.getPhsA().setValue(false);
            Op.getPhsB().setValue(false);
            Op.getPhsC().setValue(false);
        }
    }

    public double[] decomposition(CMV val) {
        double[] vectXY = new double[2];
        vectXY[0] = val.getCVal().getMag().getFloatVal().getValue() * Math.cos(val.getCVal().getAng().getFloatVal().getValue());
        vectXY[1] = val.getCVal().getMag().getFloatVal().getValue() * Math.sin(val.getCVal().getAng().getFloatVal().getValue());
        return vectXY;
    }

    //Проверка по X, то есть подставляем Y
    public boolean checkX(double[] val) {
        double k1 = line_coeff.get(1)[0];
        double b1 = line_coeff.get(1)[1];

        double k2 = line_coeff.get(2)[0];
        double b2 = line_coeff.get(2)[1];

        return (!(val[0] > (val[1] - b1) / k1)) || (!(val[0] < (val[1] - b2) / k2));

    }

    //Проверка по Y, то есть подставляем X
    public boolean checkY(double[] val) {
        double k3 = line_coeff.get(3)[0];
        double b3 = line_coeff.get(3)[1];

        double k4 = line_coeff.get(4)[0];
        double b4 = line_coeff.get(4)[1];

        double k5 = line_coeff.get(5)[0];
        double b5 = line_coeff.get(5)[1];

        double k6 = line_coeff.get(6)[0];
        double b6 = line_coeff.get(6)[1];

        return (!(val[1] < b3)) || (!(val[1] > b4)) || (!(val[1] > (k5 * val[0] + b5))) || (!(val[1] > (k6 * val[0] + b6)));
    }
}
