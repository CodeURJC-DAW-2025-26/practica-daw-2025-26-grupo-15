import type ExerciseBasicInfoDTO from "./ExerciseBasicInfoDTO";
import type UserBasicInfoDTO from "./UserBasicInfoDTO";

export default interface ListDTO {
    id: number,
    title: string;
    topic: string;
    description: string,
    lastUpdated: Date,
    owner: UserBasicInfoDTO,
    exercises: ExerciseBasicInfoDTO[],
}