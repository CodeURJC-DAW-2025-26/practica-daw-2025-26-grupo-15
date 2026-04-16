import type { ExerciseListBasicInfoDTO } from "./ExerciseListBasicInfoDTO";
import type { SolutionBasicInfoDTO } from "./SolutionBasicInfoDTO";
import type UserBasicInfoDTO from "./UserBasicInfoDTO";

export interface ExerciseDTO {
    id: number,
    title: string,
    description: string,
    numSolutions: number,
    owner: UserBasicInfoDTO,
    exerciseList: ExerciseListBasicInfoDTO,
    solutions: SolutionBasicInfoDTO[],

}