import type ExerciseBasicInfoDTO from "./ExerciseBasicInfoDTO";
import type UserBasicInfoDTO from "./UserBasicInfoDTO";

export interface SolutionDTO {
    id: number,
    name: string,
    owner: UserBasicInfoDTO,
    exercise: ExerciseBasicInfoDTO,
    lastUpdate: string,
    description: string,
    solImage: { id: 456 },
    numComments: number
}