package study.jpadata.dto;

import lombok.Data;
import study.jpadata.entity.Member;

import java.util.Optional;

import study.jpadata.entity.Team;

@Data
public class MemberDto {
    private Long id;
    private String name;
    private String teamName;

    public MemberDto(Long id, String name, String teamName) {
        this.id = id;
        this.name = name;
        this.teamName = teamName;
    }

    public MemberDto(Member member) {
        id = member.getId();
        name = member.getName();
        teamName = Optional.ofNullable(member.getTeam())
                .map(Team::getName)
                .orElse("");
    }
}
