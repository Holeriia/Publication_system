// src/types.tsx

// Тип для общих справочников (степень, звание, должность)
export type Reference = {
  id: string;
  name: string;
};

// Тип для полной информации о сотруднике (без достижений)
export type EmployeeInfo = {
  id: string;
  firstName: string;
  lastName: string;
  patronymic?: string | null;
  degree?: Reference | null;
  title?: Reference | null;
  post?: Reference | null;
  universityStartDate?: string | null; // Даты приходят в формате YYYY-MM-DD
  industryStartDate?: string | null;
  experienceComment?: string | null;
  diplomaEducation?: string | null;
};

// Тип для одного "другого достижения"
export type OtherAchievement = {
  achievementId: string;
  otherName: string;
  otherDescription?: string | null;
  comment?: string | null;
  date?: string | null; // Дата достижения в формате YYYY-MM-DD
  coAuthors: string[];
};

// Тип для ответа от бэкенда
export type EmployeeFullResponse = {
  employee: EmployeeInfo;
  otherAchievements: OtherAchievement[];
};