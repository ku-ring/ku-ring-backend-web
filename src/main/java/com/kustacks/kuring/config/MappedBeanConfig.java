package com.kustacks.kuring.config;

import com.kustacks.kuring.kuapi.CategoryName;
import com.kustacks.kuring.kuapi.api.notice.NoticeAPIClient;
import com.kustacks.kuring.kuapi.api.staff.StaffAPIClient;
import com.kustacks.kuring.kuapi.deptinfo.DeptInfo;
import com.kustacks.kuring.kuapi.deptinfo.architecture.ArchitectureDept;
import com.kustacks.kuring.kuapi.deptinfo.art_design.*;
import com.kustacks.kuring.kuapi.deptinfo.business.BusinessAdministrationDept;
import com.kustacks.kuring.kuapi.deptinfo.business.TechnologicalBusinessDept;
import com.kustacks.kuring.kuapi.deptinfo.education.*;
import com.kustacks.kuring.kuapi.deptinfo.engineering.*;
import com.kustacks.kuring.kuapi.deptinfo.ku_integrated_science.*;
import com.kustacks.kuring.kuapi.deptinfo.liberal_art.*;
import com.kustacks.kuring.kuapi.deptinfo.real_estate.RealEstateDept;
import com.kustacks.kuring.kuapi.deptinfo.sanghuo_biology.*;
import com.kustacks.kuring.kuapi.deptinfo.sanghuo_elective.ElectiveEducationCenterDept;
import com.kustacks.kuring.kuapi.deptinfo.sanghuo_elective.VolunteerCenterDept;
import com.kustacks.kuring.kuapi.deptinfo.science.ChemicalsDept;
import com.kustacks.kuring.kuapi.deptinfo.science.MathematicsDept;
import com.kustacks.kuring.kuapi.deptinfo.science.PhysicsDept;
import com.kustacks.kuring.kuapi.deptinfo.social_science.*;
import com.kustacks.kuring.kuapi.deptinfo.veterinary_medicine.PreVeterinaryDept;
import com.kustacks.kuring.kuapi.deptinfo.veterinary_medicine.VeterinaryMedicineDept;
import com.kustacks.kuring.kuapi.notice.dto.response.CommonNoticeFormatDTO;
import com.kustacks.kuring.kuapi.scrap.parser.NoticeHTMLParser;
import com.kustacks.kuring.kuapi.scrap.parser.StaffHTMLParser;
import org.jsoup.nodes.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class MappedBeanConfig {

    public MappedBeanConfig() {}

    @Configuration
    public static class APINoticeMappedBeanConfig {

        private final NoticeAPIClient<CommonNoticeFormatDTO, CategoryName> kuisNoticeAPIClient;
        private final NoticeAPIClient<CommonNoticeFormatDTO, CategoryName> libraryNoticeAPIClient;

        public APINoticeMappedBeanConfig(NoticeAPIClient<CommonNoticeFormatDTO, CategoryName> kuisNoticeAPIClient,
                                      NoticeAPIClient<CommonNoticeFormatDTO, CategoryName> libraryNoticeAPIClient) {

            this.kuisNoticeAPIClient = kuisNoticeAPIClient;
            this.libraryNoticeAPIClient = libraryNoticeAPIClient;
        }

        @Bean
        public Map<CategoryName, NoticeAPIClient<CommonNoticeFormatDTO, CategoryName>> noticeAPIClientMap() {
            HashMap<CategoryName, NoticeAPIClient<CommonNoticeFormatDTO, CategoryName>> map = new HashMap<>();
            for (CategoryName categoryName : CategoryName.values()) {
                if(categoryName.equals(CategoryName.LIBRARY)) {
                    map.put(categoryName, libraryNoticeAPIClient);
                } else {
                    map.put(categoryName, kuisNoticeAPIClient);
                }
            }
            return map;
        }
    }

    @Configuration
    public static class ScrapNoticeMappedBeanConfig {

        private final NoticeAPIClient<Document, DeptInfo> legacyPageNoticeAPIClient;
        private final NoticeAPIClient<Document, DeptInfo> recentPageNoticeAPIClient;
        private final NoticeAPIClient<Document, DeptInfo> realEstateNoticeAPIClient;

        private final NoticeHTMLParser legacyPageNoticeHTMLParser;
        private final NoticeHTMLParser recentPageNoticeHTMLParser;
        private final NoticeHTMLParser recentPageNoticeHTMLParser2;
        private final NoticeHTMLParser realEstateNoticeHTMLParser;

        private final List<DeptInfo> deptInfoList;

        public ScrapNoticeMappedBeanConfig(NoticeAPIClient<Document, DeptInfo> legacyPageNoticeAPIClient,
                                           NoticeAPIClient<Document, DeptInfo> recentPageNoticeAPIClient,
                                           NoticeAPIClient<Document, DeptInfo> realEstateNoticeAPIClient,
                                           NoticeHTMLParser legacyPageNoticeHTMLParser,
                                           NoticeHTMLParser recentPageNoticeHTMLParser,
                                           NoticeHTMLParser recentPageNoticeHTMLParser2,
                                           NoticeHTMLParser realEstateNoticeHTMLParser,
                                           List<DeptInfo> deptInfoList) {

            this.legacyPageNoticeAPIClient = legacyPageNoticeAPIClient;
            this.recentPageNoticeAPIClient = recentPageNoticeAPIClient;
            this.realEstateNoticeAPIClient = realEstateNoticeAPIClient;

            this.legacyPageNoticeHTMLParser = legacyPageNoticeHTMLParser;
            this.recentPageNoticeHTMLParser = recentPageNoticeHTMLParser;
            this.recentPageNoticeHTMLParser2 = recentPageNoticeHTMLParser2;
            this.realEstateNoticeHTMLParser = realEstateNoticeHTMLParser;

            this.deptInfoList = deptInfoList;
        }

        @Bean
        public Map<DeptInfo, NoticeAPIClient<Document, DeptInfo>> deptInfoNoticeAPIClientMap() {
            HashMap<DeptInfo, NoticeAPIClient<Document, DeptInfo>> map = new HashMap<>();
            for(DeptInfo deptInfo : deptInfoList) {
                if(deptInfo instanceof RealEstateDept) {
                    map.put(deptInfo, realEstateNoticeAPIClient);
                } else if(deptInfo instanceof ChineseDept ||
                        deptInfo instanceof MathematicsDept ||
                        deptInfo instanceof PhysicsDept ||
                        deptInfo instanceof ChemicalsDept ||
                        deptInfo instanceof KBeautyIndustryFusionDept ||
                        deptInfo instanceof InternationalTradeDept ||
                        deptInfo instanceof BiologicalSciencesDept ||
                        deptInfo instanceof LivingDesignDept ||
                        deptInfo instanceof MusicEducationDept ||
                        deptInfo instanceof EducationalTechnologyDept ||
                        deptInfo instanceof EnglishEducationDept ||
                        deptInfo instanceof StemCellRegenerativeBioTechnologyDept ||
                        deptInfo instanceof SystemBiotechnologyDept ||
                        deptInfo instanceof ElectiveEducationCenterDept) {
                    map.put(deptInfo, legacyPageNoticeAPIClient);
                } else {
                    map.put(deptInfo, recentPageNoticeAPIClient);
                }
            }
            return map;
        }

        @Bean
        public Map<DeptInfo, NoticeHTMLParser> deptInfoNoticeHTMLParserMap() {
            HashMap<DeptInfo, NoticeHTMLParser> map = new HashMap<>();
            for(DeptInfo deptInfo : deptInfoList) {
                if(deptInfo instanceof RealEstateDept) {
                    map.put(deptInfo, realEstateNoticeHTMLParser);
                } else if(deptInfo instanceof ChineseDept ||
                        deptInfo instanceof MathematicsDept ||
                        deptInfo instanceof PhysicsDept ||
                        deptInfo instanceof ChemicalsDept ||
                        deptInfo instanceof KBeautyIndustryFusionDept ||
                        deptInfo instanceof InternationalTradeDept ||
                        deptInfo instanceof BiologicalSciencesDept ||
                        deptInfo instanceof LivingDesignDept ||
                        deptInfo instanceof MusicEducationDept ||
                        deptInfo instanceof EducationalTechnologyDept ||
                        deptInfo instanceof EnglishEducationDept ||
                        deptInfo instanceof StemCellRegenerativeBioTechnologyDept ||
                        deptInfo instanceof SystemBiotechnologyDept ||
                        deptInfo instanceof ElectiveEducationCenterDept
                ) {
                    map.put(deptInfo, legacyPageNoticeHTMLParser);
                } else if(deptInfo instanceof MediaCommunicationDept ||
                        deptInfo instanceof AdvancedIndustrialFusionDept ||
                        deptInfo instanceof EconomicsDept
                ) {
                    map.put(deptInfo, recentPageNoticeHTMLParser2);
                } else {
                    map.put(deptInfo, recentPageNoticeHTMLParser);
                }
            }
            return map;
        }
    }

    @Configuration
    public static class StaffMappedBeanConfig {

        private final StaffAPIClient<Document, DeptInfo> eachDeptStaffAPIClient;
        private final StaffAPIClient<Document, DeptInfo> kuStaffAPIClient;
        private final StaffAPIClient<Document, DeptInfo> realEstateStaffAPIClient;

        private final StaffHTMLParser eachDeptStaffHTMLParser;
        private final StaffHTMLParser kuStaffHTMLParser;
        private final StaffHTMLParser realEstateStaffHTMLParser;

        private final List<DeptInfo> deptInfoList;

        public StaffMappedBeanConfig(StaffAPIClient<Document, DeptInfo> eachDeptStaffAPIClient,
                                     StaffAPIClient<Document, DeptInfo> kuStaffAPIClient,
                                     StaffAPIClient<Document, DeptInfo> realEstateStaffAPIClient,
                                     StaffHTMLParser kuStaffHTMLParser,
                                     StaffHTMLParser eachDeptStaffHTMLParser,
                                     StaffHTMLParser realEstateStaffHTMLParser,
                                     List<DeptInfo> deptInfoList) {

            this.eachDeptStaffAPIClient = eachDeptStaffAPIClient;
            this.kuStaffAPIClient = kuStaffAPIClient;
            this.realEstateStaffAPIClient = realEstateStaffAPIClient;

            this.eachDeptStaffHTMLParser = eachDeptStaffHTMLParser;
            this.kuStaffHTMLParser = kuStaffHTMLParser;
            this.realEstateStaffHTMLParser = realEstateStaffHTMLParser;

            this.deptInfoList = deptInfoList;
        }

        @Bean
        public Map<DeptInfo, StaffAPIClient<Document, DeptInfo>> deptInfoStaffAPIClientMap() {
            HashMap<DeptInfo, StaffAPIClient<Document, DeptInfo>> map = new HashMap<>();
            for(DeptInfo deptInfo : deptInfoList) {
                if(deptInfo instanceof RealEstateDept) {
                    map.put(deptInfo, realEstateStaffAPIClient);
                } else if(deptInfo instanceof LivingDesignDept || deptInfo instanceof CommunicationDesignDept) {
                    map.put(deptInfo, kuStaffAPIClient);
                } else {
                    map.put(deptInfo, eachDeptStaffAPIClient);
                }
            }
            return map;
        }

        @Bean
        public Map<DeptInfo, StaffHTMLParser> deptInfoStaffHTMLParserMap() {
            HashMap<DeptInfo, StaffHTMLParser> map = new HashMap<>();
            for(DeptInfo deptInfo : deptInfoList) {
                if(deptInfo instanceof RealEstateDept) {
                    map.put(deptInfo, realEstateStaffHTMLParser);
                } else if(deptInfo instanceof LivingDesignDept || deptInfo instanceof CommunicationDesignDept) {
                    map.put(deptInfo, kuStaffHTMLParser);
                } else {
                    map.put(deptInfo, eachDeptStaffHTMLParser);
                }
            }
            return map;
        }
    }

    @Configuration
    public static class CategoryNameDeptInfoMappedBeanConfig {

        private final KoreanDept koreanDept;
        private final EnglishDept englishDept;
        private final ChineseDept chineseDept;
        private final PhilosophyDept philosophyDept;
        private final HistoryDept historyDept;
        private final GeologyDept geologyDept;
        private final MediaCommunicationDept mediaCommunicationDept;
        private final CultureContentDept cultureContentDept;

        private final MathematicsDept mathematicsDept;
        private final PhysicsDept physicsDept;
        private final ChemicalsDept chemicalsDept;

        private final ArchitectureDept architectureDept;

        private final CivilEnvironmentDept civilEnvironmentDept;
        private final MechanicalAerospaceDept mechanicalAerospaceDept;
        private final ElectricalElectronicsDept electricalElectronicsDept;
        private final ChemicalDivisionDept chemicalDivisionDept;
        private final ComputerScienceDept computerScienceDept;
        private final IndustrialDept industrialDept;
        private final AdvancedIndustrialFusionDept advancedIndustrialFusionDept;
        private final BiologicalDept biologicalDept;
        private final KBeautyIndustryFusionDept kBeautyIndustryFusionDept;

        private final PoliticalScienceDept politicalScienceDept;
        private final EconomicsDept economicsDept;
        private final PublicAdministrationDept publicAdministrationDept;
        private final InternationalTradeDept internationalTradeDept;
        private final AppliedStatisticsDept appliedStatisticsDept;
        private final InterdisciplinaryStudiesDept interdisciplinaryStudiesDept;
        private final GlobalBusinessDept globalBusinessDept;

        private final BusinessAdministrationDept businessAdministrationDept;
        private final TechnologicalBusinessDept technologicalBusinessDept;

        private final RealEstateDept realEstateDept;

        private final EnergyDept energyDept;
        private final SmartVehicleDept smartVehicleDept;
        private final SmartICTConvergenceDept smartICTConvergenceDept;
        private final CosmeticsDept cosmeticsDept;
        private final StemCellRegenerativeBioTechnologyDept stemCellRegenerativeBioTechnologyDept;
        private final BiomedicalScienceDept biomedicalScienceDept;
        private final SystemBiotechnologyDept systemBiotechnologyDept;
        private final IntergrativeBioscienceBiotechnologyDept intergrativeBioscienceBiotechnologyDept;

        private final BiologicalSciencesDept biologicalSciencesDept;
        private final AnimalScienceTechnologyDept animalScienceTechnologyDept;
        private final CropScienceDept cropScienceDept;
        private final AnimalResourcesFoodScienceBiotechnologyDept animalResourcesFoodScienceBiotechnologyDept;
        private final FoodMarketingSafetyDept foodMarketingSafetyDept;
        private final EnvironmentalHealthScienceDept environmentalHealthScienceDept;
        private final ForestryLandscapeArchitectureDept forestryLandscapeArchitectureDept;

        private final PreVeterinaryDept preVeterinaryDept;
        private final VeterinaryMedicineDept veterinaryMedicineDept;

        private final CommunicationDesignDept communicationDesignDept;
        private final IndustrialDesignDept industrialDesignDept;
        private final ApparelDesignDept apparelDesignDept;
        private final LivingDesignDept livingDesignDept;
        private final ContemporaryArtDept contemporaryArtDept;
        private final MovingImageFilmDept movingImageFilmDept;

        private final JapaneseLanguageDept japaneseLanguageDept;
        private final MathematicsEducationDept mathematicsEducationDept;
        private final PhysicalEducationDept physicalEducationDept;
        private final MusicEducationDept musicEducationDept;
        private final EducationalTechnologyDept educationalTechnologyDept;
        private final EnglishEducationDept englishEducationDept;
        private final EducationDept educationDept;

        private final ElectiveEducationCenterDept electiveEducationCenterDept;
        private final VolunteerCenterDept volunteerCenterDept;

        public CategoryNameDeptInfoMappedBeanConfig(
                KoreanDept koreanDept,
                EnglishDept englishDept,
                ChineseDept chineseDept,
                PhilosophyDept philosophyDept,
                HistoryDept historyDept,
                GeologyDept geologyDept,
                MediaCommunicationDept mediaCommunicationDept,
                CultureContentDept cultureContentDept,
                MathematicsDept mathematicsDept,
                PhysicsDept physicsDept,
                ChemicalsDept chemicalsDept,
                ArchitectureDept architectureDept,
                CivilEnvironmentDept civilEnvironmentDept,
                MechanicalAerospaceDept mechanicalAerospaceDept,
                ElectricalElectronicsDept electricalElectronicsDept,
                ChemicalDivisionDept chemicalDivisionDept,
                ComputerScienceDept computerScienceDept,
                IndustrialDept industrialDept,
                AdvancedIndustrialFusionDept advancedIndustrialFusionDept,
                BiologicalDept biologicalDept,
                KBeautyIndustryFusionDept kBeautyIndustryFusionDept,
                PoliticalScienceDept politicalScienceDept,
                EconomicsDept economicsDept,
                PublicAdministrationDept publicAdministrationDept,
                InternationalTradeDept internationalTradeDept,
                AppliedStatisticsDept appliedStatisticsDept,
                InterdisciplinaryStudiesDept interdisciplinaryStudiesDept,
                GlobalBusinessDept globalBusinessDept,
                BusinessAdministrationDept businessAdministrationDept,
                TechnologicalBusinessDept technologicalBusinessDept,
                RealEstateDept realEstateDept,
                EnergyDept energyDept,
                SmartVehicleDept smartVehicleDept,
                SmartICTConvergenceDept smartICTConvergenceDept,
                CosmeticsDept cosmeticsDept,
                StemCellRegenerativeBioTechnologyDept stemCellRegenerativeBioTechnologyDept,
                BiomedicalScienceDept biomedicalScienceDept,
                SystemBiotechnologyDept systemBiotechnologyDept,
                IntergrativeBioscienceBiotechnologyDept intergrativeBioscienceBiotechnologyDept,
                BiologicalSciencesDept biologicalSciencesDept,
                AnimalScienceTechnologyDept animalScienceTechnologyDept,
                CropScienceDept cropScienceDept,
                AnimalResourcesFoodScienceBiotechnologyDept animalResourcesFoodScienceBiotechnologyDept,
                FoodMarketingSafetyDept foodMarketingSafetyDept,
                EnvironmentalHealthScienceDept environmentalHealthScienceDept,
                ForestryLandscapeArchitectureDept forestryLandscapeArchitectureDept,
                PreVeterinaryDept preVeterinaryDept,
                VeterinaryMedicineDept veterinaryMedicineDept,
                CommunicationDesignDept communicationDesignDept,
                IndustrialDesignDept industrialDesignDept,
                ApparelDesignDept apparelDesignDept,
                LivingDesignDept livingDesignDept,
                ContemporaryArtDept contemporaryArtDept,
                MovingImageFilmDept movingImageFilmDept,
                JapaneseLanguageDept japaneseLanguageDept,
                MathematicsEducationDept mathematicsEducationDept,
                PhysicalEducationDept physicalEducationDept,
                MusicEducationDept musicEducationDept,
                EducationalTechnologyDept educationalTechnologyDept,
                EnglishEducationDept englishEducationDept,
                EducationDept educationDept,
                ElectiveEducationCenterDept electiveEducationCenterDept,
                VolunteerCenterDept volunteerCenterDept) {

            this.koreanDept = koreanDept;
            this.englishDept = englishDept;
            this.chineseDept = chineseDept;
            this.philosophyDept = philosophyDept;
            this.historyDept = historyDept;
            this.geologyDept = geologyDept;
            this.mediaCommunicationDept = mediaCommunicationDept;
            this.cultureContentDept = cultureContentDept;

            this.mathematicsDept = mathematicsDept;
            this.physicsDept = physicsDept;
            this.chemicalsDept = chemicalsDept;

            this.architectureDept = architectureDept;

            this.civilEnvironmentDept = civilEnvironmentDept;
            this.mechanicalAerospaceDept = mechanicalAerospaceDept;
            this.electricalElectronicsDept = electricalElectronicsDept;
            this.chemicalDivisionDept = chemicalDivisionDept;
            this.computerScienceDept = computerScienceDept;
            this.industrialDept = industrialDept;
            this.advancedIndustrialFusionDept = advancedIndustrialFusionDept;
            this.biologicalDept = biologicalDept;
            this.kBeautyIndustryFusionDept = kBeautyIndustryFusionDept;

            this.politicalScienceDept = politicalScienceDept;
            this.economicsDept = economicsDept;
            this.publicAdministrationDept = publicAdministrationDept;
            this.internationalTradeDept = internationalTradeDept;
            this.appliedStatisticsDept = appliedStatisticsDept;
            this.interdisciplinaryStudiesDept = interdisciplinaryStudiesDept;
            this.globalBusinessDept = globalBusinessDept;

            this.businessAdministrationDept = businessAdministrationDept;
            this.technologicalBusinessDept = technologicalBusinessDept;

            this.realEstateDept = realEstateDept;

            this.energyDept = energyDept;
            this.smartVehicleDept = smartVehicleDept;
            this.smartICTConvergenceDept = smartICTConvergenceDept;
            this.cosmeticsDept = cosmeticsDept;
            this.stemCellRegenerativeBioTechnologyDept = stemCellRegenerativeBioTechnologyDept;
            this.biomedicalScienceDept = biomedicalScienceDept;
            this.systemBiotechnologyDept = systemBiotechnologyDept;
            this.intergrativeBioscienceBiotechnologyDept = intergrativeBioscienceBiotechnologyDept;

            this.biologicalSciencesDept = biologicalSciencesDept;
            this.animalScienceTechnologyDept = animalScienceTechnologyDept;
            this.cropScienceDept = cropScienceDept;
            this.animalResourcesFoodScienceBiotechnologyDept = animalResourcesFoodScienceBiotechnologyDept;
            this.foodMarketingSafetyDept = foodMarketingSafetyDept;
            this.environmentalHealthScienceDept = environmentalHealthScienceDept;
            this.forestryLandscapeArchitectureDept = forestryLandscapeArchitectureDept;

            this.preVeterinaryDept = preVeterinaryDept;
            this.veterinaryMedicineDept = veterinaryMedicineDept;

            this.communicationDesignDept = communicationDesignDept;
            this.industrialDesignDept = industrialDesignDept;
            this.apparelDesignDept = apparelDesignDept;
            this.livingDesignDept = livingDesignDept;
            this.contemporaryArtDept = contemporaryArtDept;
            this.movingImageFilmDept = movingImageFilmDept;

            this.japaneseLanguageDept = japaneseLanguageDept;
            this.mathematicsEducationDept = mathematicsEducationDept;
            this.physicalEducationDept = physicalEducationDept;
            this.musicEducationDept = musicEducationDept;
            this.educationalTechnologyDept = educationalTechnologyDept;
            this.englishEducationDept =englishEducationDept;
            this.educationDept = educationDept;

            this.electiveEducationCenterDept = electiveEducationCenterDept;
            this.volunteerCenterDept = volunteerCenterDept;
        }

        @Bean
        public Map<CategoryName, DeptInfo> categoryNameDeptInfoMap() {
            Map<CategoryName, DeptInfo> map = new HashMap<>();

            map.put(CategoryName.KOREAN, koreanDept);
            map.put(CategoryName.ENGLISH, englishDept);
            map.put(CategoryName.CHINESE, chineseDept);
            map.put(CategoryName.PHILOSOPHY, philosophyDept);
            map.put(CategoryName.HISTORY, historyDept);
            map.put(CategoryName.GEOLOGY, geologyDept);
            map.put(CategoryName.MEDIA_COMM, mediaCommunicationDept);
            map.put(CategoryName.CULTURE_CONT, cultureContentDept);

            map.put(CategoryName.MATH, mathematicsDept);
            map.put(CategoryName.PHYSICS, physicsDept);
            map.put(CategoryName.CHEMICALS, chemicalsDept);

            map.put(CategoryName.ARCHITECTURE, architectureDept);

            map.put(CategoryName.CIVIL_ENV, civilEnvironmentDept);
            map.put(CategoryName.MECH_AERO, mechanicalAerospaceDept);
            map.put(CategoryName.ELEC_ELEC, electricalElectronicsDept);
            map.put(CategoryName.CHEMI_DIV, chemicalDivisionDept);
            map.put(CategoryName.COMPUTER, computerScienceDept);
            map.put(CategoryName.INDUSTRIAL, industrialDept);
            map.put(CategoryName.ADV_INDUSTRIAL, advancedIndustrialFusionDept);
            map.put(CategoryName.BIOLOGICAL, biologicalDept);
            map.put(CategoryName.KBEAUTY, kBeautyIndustryFusionDept);

            map.put(CategoryName.POLITICS, politicalScienceDept);
            map.put(CategoryName.ECONOMICS, economicsDept);
            map.put(CategoryName.ADMINISTRATION, publicAdministrationDept);
            map.put(CategoryName.INT_TRADE, internationalTradeDept);
            map.put(CategoryName.STATISTICS, appliedStatisticsDept);
            map.put(CategoryName.DISCI_STUDIES, interdisciplinaryStudiesDept);
            map.put(CategoryName.GLOBAL_BUSI, globalBusinessDept);

            map.put(CategoryName.BUIS_ADMIN, businessAdministrationDept);
            map.put(CategoryName.TECH_BUSI, technologicalBusinessDept);

            map.put(CategoryName.REAL_ESTATE, realEstateDept);

            map.put(CategoryName.ENERGY, energyDept);
            map.put(CategoryName.SMART_VEHICLE, smartVehicleDept);
            map.put(CategoryName.SMART_ICT, smartICTConvergenceDept);

            map.put(CategoryName.COSMETICS, cosmeticsDept);
            map.put(CategoryName.STEM_REGEN, stemCellRegenerativeBioTechnologyDept);
            map.put(CategoryName.BIO_MEDICAL, biomedicalScienceDept);
            map.put(CategoryName.SYSTEM_BIO_TECH, systemBiotechnologyDept);
            map.put(CategoryName.INT_BIO_TECH, intergrativeBioscienceBiotechnologyDept);

            map.put(CategoryName.BIO_SCIENCE, biologicalSciencesDept);
            map.put(CategoryName.ANIMAL_SCIENCE, animalScienceTechnologyDept);
            map.put(CategoryName.CROP_SCIENCE, cropScienceDept);
            map.put(CategoryName.ANIMAL_RESOURCES, animalResourcesFoodScienceBiotechnologyDept);
            map.put(CategoryName.FOOD_MARKETING, foodMarketingSafetyDept);
            map.put(CategoryName.ENV_HEALTH_SCIENCE, environmentalHealthScienceDept);
            map.put(CategoryName.FORESTRY_LANDSCAPE_ARCH, forestryLandscapeArchitectureDept);

            map.put(CategoryName.VET_PRE, preVeterinaryDept);
            map.put(CategoryName.VET_MEDICINE, veterinaryMedicineDept);

            map.put(CategoryName.COMM_DESIGN, communicationDesignDept);
            map.put(CategoryName.IND_DESIGN, industrialDesignDept);
            map.put(CategoryName.APPAREL_DESIGN, apparelDesignDept);
            map.put(CategoryName.LIVING_DESIGN, livingDesignDept);
            map.put(CategoryName.CONT_ART, contemporaryArtDept);
            map.put(CategoryName.MOV_IMAGE, movingImageFilmDept);

            map.put(CategoryName.JAPANESE_EDU, japaneseLanguageDept);
            map.put(CategoryName.MATH_EDU, mathematicsEducationDept);
            map.put(CategoryName.PHY_EDU, physicalEducationDept);
            map.put(CategoryName.MUSIC_EDU, musicEducationDept);
            map.put(CategoryName.EDU_TECH, educationalTechnologyDept);
            map.put(CategoryName.ENGLISH_EDU, englishEducationDept);
            map.put(CategoryName.EDUCATION, educationDept);

            map.put(CategoryName.ELE_EDU_CENTER, electiveEducationCenterDept);
            map.put(CategoryName.VOLUNTEER, volunteerCenterDept);

            return map;
        }
    }
}
