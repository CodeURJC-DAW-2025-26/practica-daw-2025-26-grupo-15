import type ExerciseBasicInfoDTO from "./ExerciseBasicInfoDTO";
import type UserBasicInfoDTO from "./UserBasicInfoDTO";

export default interface ListDTO {
    id: string,
    title: string;
    topic: string;
    description: string,
    lastUpdate: string,
    owner: UserBasicInfoDTO,
    exercises: ExerciseBasicInfoDTO[],
}
