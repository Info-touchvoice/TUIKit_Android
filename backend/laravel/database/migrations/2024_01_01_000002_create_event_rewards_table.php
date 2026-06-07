<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('event_rewards', function (Blueprint $table) {
            $table->id();
            $table->unsignedBigInteger('event_id');
            $table->integer('reward_tier')->default(1);
            $table->enum('reward_type', [
                'coins', 'diamonds', 'vip_days', 'frames', 'badges', 'gifts', 'custom'
            ]);
            $table->integer('reward_amount');
            $table->string('reward_name', 255)->nullable();
            $table->text('reward_description')->nullable();
            $table->enum('trigger_type', [
                'recharge_amount', 'spending_amount', 'login_streak',
                'ranking_position', 'task_completion'
            ]);
            $table->integer('trigger_condition');
            $table->integer('max_claims')->nullable();
            $table->integer('claims_count')->default(0);
            $table->timestamps();

            $table->index('event_id');
            $table->index('reward_type');
            $table->foreign('event_id')->references('id')->on('events')->onDelete('cascade');
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('event_rewards');
    }
};
