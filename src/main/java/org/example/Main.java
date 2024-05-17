package org.example;

import org.example.iec61850.lodicalNodes.LN;
import org.example.iec61850.lodicalNodes.function_protection.RDIR;
import org.example.iec61850.lodicalNodes.hmi.NHMI;
import org.example.iec61850.lodicalNodes.hmi.other.NHMISignal;
import org.example.iec61850.lodicalNodes.measurement.MMXU_DZ;
import org.example.iec61850.lodicalNodes.measurement.MSQI;
import org.example.iec61850.lodicalNodes.protection.PDIS;
import org.example.iec61850.lodicalNodes.protection.RPSB;
import org.example.iec61850.lodicalNodes.supervisory_control.CSWI;
import org.example.iec61850.lodicalNodes.switchgear.XCBR;
import org.example.iec61850.lodicalNodes.system_logic_nodes.LSVC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    private static final List<LN> logicalNode = new ArrayList<>();
    private static String path = "C:\\Users\\Aglomiras\\Изображения\\Рабочий стол\\AlgoritmRZAProgrammRealize\\Лабораторная №2\\Опыты\\";
    private static String name = "KZ7"; //1,2,3,4,5,6,7

    public static void main(String[] args) throws Exception {
        /**Создаем узел LSVC и добавляем узел в лист узлов*/
        LSVC lsvc = new LSVC();
        lsvc.setPath(path);
        lsvc.setFileName(name);
        logicalNode.add(lsvc);

        /**Создаем узел MMXU и добавляем узел в лист узлов*/
        MMXU_DZ mmxu = new MMXU_DZ();
        mmxu.UaInst = lsvc.getOut().get(0);
        mmxu.UbInst = lsvc.getOut().get(1);
        mmxu.UcInst = lsvc.getOut().get(2);
        mmxu.IaInst = lsvc.getOut().get(3);
        mmxu.IbInst = lsvc.getOut().get(4);
        mmxu.IcInst = lsvc.getOut().get(5);
        logicalNode.add(mmxu);

        /**MSQI*/
        MSQI msqi = new MSQI();
        msqi.I = mmxu.getA();
        msqi.U = mmxu.getPNV();
        logicalNode.add(msqi);

        /**RDIR*/
        RDIR rdir = new RDIR();
        rdir.SeqA = msqi.getSeqA();
        rdir.SeqV = msqi.getSeqV();
        logicalNode.add(rdir);

        /**RPSB*/
        RPSB rpsb = new RPSB();
        rpsb.Z = mmxu.Z;
        rpsb.modZ.getSetMag().getFloatVal().setValue(600.0);
        logicalNode.add(rpsb);

        //Направленные ступени ДЗ
        //1
        PDIS pdisDir1 = new PDIS();
        pdisDir1.getPhStr().getSetMag().getFloatVal().setValue(85.2414d);
        pdisDir1.getOpDlTmms().getSetVal().setValue(100);
        pdisDir1.blkDZ.getGeneral().setValue(rpsb.getOp().getGeneral().getValue());
        pdisDir1.setZ(mmxu.getZ());
        Map<Integer, double[]> graph_constants = new HashMap<>();
        graph_constants.put(1, new double[]{2.75, 20});
        graph_constants.put(2, new double[]{2.75, -20});
        graph_constants.put(3, new double[]{0, 10});
        graph_constants.put(4, new double[]{0, -10});
        graph_constants.put(5, new double[]{-2.14, 0});
        graph_constants.put(6, new double[]{-0.5, 0});
        pdisDir1.line_coeff = graph_constants;
        logicalNode.add(pdisDir1);

        //2
        PDIS pdisDir2 = new PDIS();
        pdisDir2.getPhStr().getSetMag().getFloatVal().setValue(85.2414d);
        pdisDir2.getOpDlTmms().getSetVal().setValue(10);
        pdisDir2.blkDZ.getGeneral().setValue(rpsb.getOp().getGeneral().getValue());
        pdisDir2.setZ(mmxu.getZ());
        Map<Integer, double[]> graph_constants1 = new HashMap<>();
        graph_constants1.put(1, new double[]{2.75, 30});
        graph_constants1.put(2, new double[]{2.75, -30});
        graph_constants1.put(3, new double[]{0, 20});
        graph_constants1.put(4, new double[]{0, -20});
        graph_constants1.put(5, new double[]{-2.14, 0});
        graph_constants1.put(6, new double[]{-0.5, 0});
        pdisDir2.line_coeff = graph_constants1;
        logicalNode.add(pdisDir2);

        //3
        PDIS pdisDir3 = new PDIS();
        pdisDir3.getPhStr().getSetMag().getFloatVal().setValue(85.2414d);
        pdisDir3.getOpDlTmms().getSetVal().setValue(500);
        pdisDir3.blkDZ.getGeneral().setValue(rpsb.getOp().getGeneral().getValue());
        pdisDir3.setZ(mmxu.getZ());
        Map<Integer, double[]> graph_constants2 = new HashMap<>();
        graph_constants2.put(1, new double[]{2.75, 40});
        graph_constants2.put(2, new double[]{2.75, -40});
        graph_constants2.put(3, new double[]{0, 40});
        graph_constants2.put(4, new double[]{0, -40});
        graph_constants2.put(5, new double[]{-2.14, 0});
        graph_constants2.put(6, new double[]{-0.5, 0});
        pdisDir3.line_coeff = graph_constants2;
        logicalNode.add(pdisDir3);

        //Ненаправленные ступени ДЗ
        //I
        PDIS pdis1 = new PDIS();
        pdis1.getPhStr().getSetMag().getFloatVal().setValue(85.2414d);
        pdis1.getOpDlTmms().getSetVal().setValue(10);
        pdis1.blkDZ.getGeneral().setValue(rpsb.getOp().getGeneral().getValue());
        pdis1.setZ(mmxu.getZ());
        Map<Integer, double[]> graph_constants3 = new HashMap<>();
        graph_constants3.put(1, new double[]{2.75, 2000});
        graph_constants3.put(2, new double[]{2.75, -2000});
        graph_constants3.put(3, new double[]{0, 1000});
        graph_constants3.put(4, new double[]{0, -1000});
        graph_constants3.put(5, new double[]{-2.14, -100}); //Загрубление направленных характеристик
        graph_constants3.put(6, new double[]{-0.5, -100});
        pdis1.line_coeff = graph_constants3;
        logicalNode.add(pdis1);

        //II
        PDIS pdis2 = new PDIS();
        pdis2.getPhStr().getSetMag().getFloatVal().setValue(85.2414d);
        pdis2.getOpDlTmms().getSetVal().setValue(200);
        pdis2.blkDZ.getGeneral().setValue(rpsb.getOp().getGeneral().getValue());
        pdis2.setZ(mmxu.getZ());
        Map<Integer, double[]> graph_constants4 = new HashMap<>();
        graph_constants4.put(1, new double[]{2.75, 30});
        graph_constants4.put(2, new double[]{2.75, -30});
        graph_constants4.put(3, new double[]{0, 20});
        graph_constants4.put(4, new double[]{0, -20});
        graph_constants4.put(5, new double[]{-2.14, -100});
        graph_constants4.put(6, new double[]{-0.5, -100});
        pdis2.line_coeff = graph_constants4;
        logicalNode.add(pdis2);

        /**Узел контроля сигналов на отключение*/
        CSWI cswi = new CSWI();
        /**Добавляем информацию о сигнале на отключение оборудования от защит*/
        cswi.getOpOpnList().add(pdisDir1.getOp());
        cswi.getOpOpnList().add(pdisDir2.getOp());
        cswi.getOpOpnList().add(pdisDir3.getOp());
        cswi.getOpOpnList().add(pdis1.getOp());
        cswi.getOpOpnList().add(pdis2.getOp());
        logicalNode.add(cswi);

        XCBR xcbr = new XCBR();
        xcbr.setPos(cswi.getPos());
        logicalNode.add(xcbr);

        /**Вывод самих сигналов всех фаз*/
        NHMI nhmiMMXU = new NHMI();
        nhmiMMXU.addSignals("SignalIA", new NHMISignal("ia", mmxu.IaInst.getInstMag().getFloatVal()));
        nhmiMMXU.addSignals("SignalIB", new NHMISignal("ib", mmxu.IbInst.getInstMag().getFloatVal()));
        nhmiMMXU.addSignals("SignalIC", new NHMISignal("ic", mmxu.IcInst.getInstMag().getFloatVal()));

        nhmiMMXU.addSignals("SignalUA", new NHMISignal("ua", mmxu.UaInst.getInstMag().getFloatVal()));
        nhmiMMXU.addSignals("SignalUB", new NHMISignal("ub", mmxu.UbInst.getInstMag().getFloatVal()));
        nhmiMMXU.addSignals("SignalUC", new NHMISignal("uc", mmxu.UcInst.getInstMag().getFloatVal()));
        logicalNode.add(nhmiMMXU);

        /**Вывод действующих значений фаз, угол и уставки защит*/
        NHMI nhmiPDIS = new NHMI();
        nhmiPDIS.addSignals("Действующее, угол и уставки I_A",
                new NHMISignal("RMS_IA", mmxu.getA().getPhsA().getInstCVal().getMag().getFloatVal()),
                new NHMISignal("Angular_IA", mmxu.getA().getPhsA().getInstCVal().getAng().getFloatVal())
        );
        nhmiPDIS.addSignals("Действующее, угол и уставки I_B",
                new NHMISignal("Phase_IB", mmxu.getA().getPhsB().getInstCVal().getMag().getFloatVal()),
                new NHMISignal("Angular_IB", mmxu.getA().getPhsB().getInstCVal().getAng().getFloatVal())
        );
        nhmiPDIS.addSignals("Действующее, угол и уставки I_C",
                new NHMISignal("Phase_IC", mmxu.getA().getPhsC().getInstCVal().getMag().getFloatVal()),
                new NHMISignal("Angular_IC", mmxu.getA().getPhsC().getInstCVal().getAng().getFloatVal())
        );
        nhmiPDIS.addSignals("Действующее, угол и уставки U_A",
                new NHMISignal("RMS_UA", mmxu.getPNV().getPhsA().getInstCVal().getMag().getFloatVal()),
                new NHMISignal("Angular_UA", mmxu.getPNV().getPhsA().getInstCVal().getAng().getFloatVal())
        );
        nhmiPDIS.addSignals("Действующее, угол и уставки U_B",
                new NHMISignal("RMS_UB", mmxu.getPNV().getPhsA().getInstCVal().getMag().getFloatVal()),
                new NHMISignal("Angular_UB", mmxu.getPNV().getPhsA().getInstCVal().getAng().getFloatVal())
        );
        nhmiPDIS.addSignals("Действующее, угол и уставки U_C",
                new NHMISignal("RMS_UC", mmxu.getPNV().getPhsA().getInstCVal().getMag().getFloatVal()),
                new NHMISignal("Angular_UC", mmxu.getPNV().getPhsA().getInstCVal().getAng().getFloatVal())
        );
        logicalNode.add(nhmiPDIS);

        NHMI nhmiImpedance = new NHMI();
        nhmiImpedance.addSignals(
                "Действующее и угл сопротивления в фазe A",
                new NHMISignal("ZRmsPhsA",
                        mmxu.getZ().getPhsA().getCVal().getMag().getFloatVal()),
                new NHMISignal("ZAngValuePhsA",
                        mmxu.getZ().getPhsA().getCVal().getAng().getFloatVal()));
        nhmiImpedance.addSignals(
                "Действующее и угл сопротивления в фазe B",
                new NHMISignal("ZRmsValuePhsB",
                        mmxu.getZ().getPhsB().getCVal().getMag().getFloatVal()),
                new NHMISignal("ZAngValuePhsB",
                        mmxu.getZ().getPhsB().getCVal().getAng().getFloatVal()));
        nhmiImpedance.addSignals(
                "Действующее и угл сопротивления в фазe C",
                new NHMISignal("ZRmsValuePhsC",
                        mmxu.getZ().getPhsC().getCVal().getMag().getFloatVal()),
                new NHMISignal("ZAngValuePhsC",
                        mmxu.getZ().getPhsC().getCVal().getAng().getFloatVal()));
        logicalNode.add(nhmiImpedance);

        NHMI nhmiDiscretSignals = new NHMI();
        nhmiDiscretSignals.addSignals(
                "Сигналы 1 ступени направленной ДЗ",
                new NHMISignal("OpDir1", pdisDir1.getOp().getGeneral()),
                new NHMISignal("StrDir1", pdisDir1.getStr().getGeneral()));
        nhmiDiscretSignals.addSignals(
                "Сигналы 2 ступени направленной ДЗ",
                new NHMISignal("OpDir2", pdisDir2.getOp().getGeneral()),
                new NHMISignal("StrDir2", pdisDir2.getStr().getGeneral()));
        nhmiDiscretSignals.addSignals(
                "Сигналы 3 ступени направленной ДЗ",
                new NHMISignal("Op1", pdisDir3.getOp().getGeneral()),
                new NHMISignal("Str1", pdisDir3.getStr().getGeneral()));
        nhmiDiscretSignals.addSignals(
                "Сигналы 1 ступени ненаправленной ДЗ",
                new NHMISignal("Op2", pdis1.getOp().getGeneral()),
                new NHMISignal("Str2", pdis1.getStr().getGeneral()));
        nhmiDiscretSignals.addSignals(
                "Сигналы 2 ступени ненаправленной ДЗ",
                new NHMISignal("Op3", pdis1.getOp().getGeneral()),
                new NHMISignal("Str3", pdis1.getStr().getGeneral()));
        nhmiDiscretSignals.addSignals(
                "Сигнал блокировки от качаний",
                new NHMISignal("blk", rpsb.getOp().getGeneral()));
        logicalNode.add(nhmiDiscretSignals);

        while (lsvc.hasNext()) {
            logicalNode.forEach(LN::process);
        }
    }
}
