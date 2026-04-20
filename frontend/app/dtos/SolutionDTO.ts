import type ExerciseBasicInfoDTO from "./ExerciseBasicInfoDTO";

export interface SolutionDTO {
    id: number,
    name: string,
    owner: { name: string },
    exercise: ExerciseBasicInfoDTO,
    lastUpdate: string,
    description: string,
    solImage: { id: 456 },
    numComments: number
}